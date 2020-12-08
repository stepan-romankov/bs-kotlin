package blocksize.assignment.data.sql

import blocksize.assignment.model.ApiKeyId
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

internal class DbDeleteApiKeyOperationTest : DbTest() {

    @Test
    fun testDelete() = runBlocking {
      val operation = DbDeleteApiKeyOperation(tm)
      val id = tm.tx {
        ApiKeysTable.insert {
          it[userId] = "u"
          it[exchange] = "e"
          it[apiKey] = "k".encodeToByteArray()
          it[secret] = "s".encodeToByteArray()
          it[apiKeyPreview] = "p"
        }.get(ApiKeysTable.id)
      }
      tm.tx {
        operation.delete(ApiKeyId(id))
      }
    }

  @Test
  fun testDeleteIdempotency() = runBlocking {
    val operation = DbDeleteApiKeyOperation(tm)
    tm.tx {
      operation.delete(ApiKeyId(UUID.randomUUID().toString()))
    }
  }
}
