package blocksize.assignment

import blocksize.assignment.crypt.AESCrypt
import blocksize.assignment.data.sql.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


internal class ApikeyGrpcServiceTest : DbTest() {
  lateinit var aes: AESCrypt
  lateinit var saveOp: DbSaveApiKeyOperation
  lateinit var deleteOp: DbDeleteApiKeyOperation
  lateinit var findQ: DbFindByUserQuery
  lateinit var getByIdQ: DbGetByIdQuery
  lateinit var service: ApikeyGrpcService

  @BeforeEach
  override fun setUp() {
    super.setUp()

    aes = AESCrypt("123456789012345678901234")
    saveOp = DbSaveApiKeyOperation(tm)
    deleteOp = DbDeleteApiKeyOperation(tm)
    findQ = DbFindByUserQuery(tm)
    getByIdQ = DbGetByIdQuery(tm)
    service = ApikeyGrpcService(findQ, getByIdQ, saveOp, deleteOp, aes, aes)
  }



  @Test
  fun testApikeyFlow() {
      runBlocking {
        val addResult = service.addApikey(Service.AddApikeyRequest.newBuilder()
          .setUserId("u")
          .setExchange("e")
          .setApikey("k123456789")
          .setSecret("s").build())

        val keys = service.listApikeys(Service.ListApikeysRequest.newBuilder().setUserId("u").build())
        assertEquals(1, keys.apikeysCount)
        assertEquals("k1234", keys.getApikeys(0).apikeyPreview)
        assertEquals("e", keys.getApikeys(0).exchange)
        assertEquals(addResult.apikeyId, keys.getApikeys(0).apikeyId)

        val key = service.getApikey(Service.GetApikeyRequest.newBuilder().setApikeyId(addResult.apikeyId).build())
        assertEquals("k123456789", key.apikey.apikey)
        assertEquals("s", key.apikey.secret)
        assertEquals("k1234", key.apikey.apikeyDetails.apikeyPreview)
        assertEquals("e", keys.getApikeys(0).exchange)
        assertEquals(addResult.apikeyId, keys.getApikeys(0).apikeyId)
        service.deleteApikey(Service.DeleteApikeyRequest.newBuilder().setApikeyId(addResult.apikeyId).build())
      }
  }
}
