package blocksize.assignment.data.sql

import blocksize.assignment.data.*
import blocksize.assignment.model.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.slf4j.LoggerFactory

class DbSaveApiKeyOperation(private val tm : TransactionManager) : SaveApiKeyOperation {
  val log = LoggerFactory.getLogger(this.javaClass)

  override suspend fun save(
      userId: UserId,
      exchange: Exchange,
      apiKey: EncodedApiKey,
      apiKeyPreview: ApiKeyPreview,
      secret: EncodedSecret
  ): ApiKeyId {
    return tm.tx {
      try {
          val id = ApiKeyId(ApiKeysTable.insert {
              //.insert {
              it[ApiKeysTable.userId] = userId.value
              it[ApiKeysTable.exchange] = exchange.value
              it[ApiKeysTable.apiKeyPreview] = apiKeyPreview.value
              it[ApiKeysTable.apiKey] = apiKey.value
              it[ApiKeysTable.secret] = secret.value
          }
              .get(ApiKeysTable.id))

        log.info("Api key {} created for user {} exchange {}", id, userId, exchange)

        return@tx id
      } catch (e : ExposedSQLException) {
        if (e.duplicateKey) {
          throw DuplicateEntityException()
        }
        else {
          throw e
        }
      }
    }
  }
}

class DbDeleteApiKeyOperation(private val tm : TransactionManager) : DeleteApiKeyOperation {
  val log = LoggerFactory.getLogger(this.javaClass)

  override suspend fun delete(apiKeyId: ApiKeyId) {
    tm.tx {
      ApiKeysTable.deleteWhere { ApiKeysTable.id eq apiKeyId.value }
    }
    log.info("Api key {} deleted ", apiKeyId)
  }
}
