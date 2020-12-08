package blocksize.assignment.data.sql

import blocksize.assignment.data.FindByUserQuery
import blocksize.assignment.data.GetByIdQuery
import blocksize.assignment.model.*
import org.jetbrains.exposed.sql.select
import org.slf4j.LoggerFactory

class DbFindByUserQuery(private val tm: TransactionManager) : FindByUserQuery {
  override suspend fun execute(userId: UserId, block: (FindByUserQuery.Result) -> Unit) {
    tm.tx {
      ApiKeysTable.select { ApiKeysTable.userId eq userId.value }.forEach {
        block(object : FindByUserQuery.Result {
          override val apiKeyId: ApiKeyId
            get() = ApiKeyId(it[ApiKeysTable.id])
          override val exchange: Exchange
            get() = Exchange(it[ApiKeysTable.exchange])
          override val apiKeyPreview: ApiKeyPreview
            get() = ApiKeyPreview(it[ApiKeysTable.apiKeyPreview])
        })
      }
    }
  }

}

class DbGetByIdQuery(private val tm: TransactionManager) : GetByIdQuery {
  val log = LoggerFactory.getLogger(this.javaClass)

  override suspend fun execute(apiKeyId: ApiKeyId): GetByIdQuery.Result? {
    val result = tm.tx {
      ApiKeysTable
        .select { ApiKeysTable.id eq apiKeyId.value }
        .map {
          GetByIdQuery.Result(
            ApiKeyId(it[ApiKeysTable.id]),
            Exchange(it[ApiKeysTable.exchange]),
            ApiKeyPreview(it[ApiKeysTable.apiKeyPreview]),
            EncodedApiKey(it[ApiKeysTable.apiKey]),
            EncodedSecret(it[ApiKeysTable.secret])
          )
        }
        .firstOrNull()
    }

    log.info("Api key {} requested ", apiKeyId)

    return result
  }

}








