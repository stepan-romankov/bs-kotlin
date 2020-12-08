package blocksize.assignment.data.sql

import blocksize.assignment.model.*
import org.jetbrains.exposed.sql.Table
import java.util.*

object ApiKeysTable : Table() {
  val userId = varchar("user_id", UserId.MAX_LENGTH)
  val exchange = varchar("exchange", Exchange.MAX_LENGTH)
  val id = varchar("id", ApiKeyId.MAX_LENGTH).clientDefault { UUID.randomUUID().toString() }
  val apiKeyPreview = varchar("api_key_preview", ApiKeyPreview.MAX_LENGTH)
  val apiKey = binary("api_key", ApiKey.MAX_LENGTH)
  val secret = binary("secret", Secret.MAX_LENGTH)
  override val primaryKey = PrimaryKey(userId, exchange)
}
