package blocksize.assignment.data.sql

import blocksize.assignment.data.DuplicateEntityException
import blocksize.assignment.model.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals


internal class DbSaveApiKeyOperationTest : DbTest() {

  @Test
  fun testSave(): Unit = runBlocking {
    val operation = DbSaveApiKeyOperation(tm)

    operation.save(
      UserId("u"),
      Exchange("e"),
      EncodedApiKey("ak".encodeToByteArray()),
      ApiKeyPreview("p"),
      EncodedSecret("s".encodeToByteArray())
    )

    var count = tm.tx {
      ApiKeysTable.selectAll().count()
    }
    assertEquals(1, count)

    count = tm.tx {
      ApiKeysTable.select {
        (ApiKeysTable.userId eq "u") and
          (ApiKeysTable.exchange eq "e") and
          (ApiKeysTable.apiKey eq "ak".encodeToByteArray()) and
          (ApiKeysTable.secret eq "s".encodeToByteArray()) and
          (ApiKeysTable.apiKeyPreview eq "p")
      }.count()
    }
    assertEquals(1, count)
  }

  @Test
  fun testSaveDuplicate() {
    val operation = DbSaveApiKeyOperation(tm)
    assertThrows<DuplicateEntityException> {
      runBlocking {
        repeat(2) {
          operation.save(
            UserId("u"),
            Exchange("e"),
            EncodedApiKey("ak".encodeToByteArray()),
            ApiKeyPreview("ak"),
            EncodedSecret("se".encodeToByteArray())
          )
        }
      }
    }
  }
}
