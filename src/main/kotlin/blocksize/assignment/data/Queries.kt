package blocksize.assignment.data

import blocksize.assignment.model.*

interface GetByIdQuery {
  suspend fun execute(apiKeyId: ApiKeyId): Result?

  class Result(
    val apiKeyId: ApiKeyId,
    val exchange: Exchange,
    val apiKeyPreview: ApiKeyPreview,
    val apiKey: EncodedApiKey,
    val secret: EncodedSecret
  )
}

interface FindByUserQuery {
  suspend fun execute(userId: UserId, block: (Result) -> Unit)

  interface Result {
    val apiKeyId: ApiKeyId
    val exchange: Exchange
    val apiKeyPreview: ApiKeyPreview
  }
}
