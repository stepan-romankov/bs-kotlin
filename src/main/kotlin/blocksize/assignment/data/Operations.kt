package blocksize.assignment.data

import blocksize.assignment.model.*
import blocksize.assignment.model.ApiKeyPreview

interface SaveApiKeyOperation {
  suspend fun save(
    userId: UserId,
    exchange: Exchange,
    apiKey: EncodedApiKey,
    apiKeyPreview: ApiKeyPreview,
    secret: EncodedSecret
  ) : ApiKeyId
}


interface DeleteApiKeyOperation {
  suspend fun delete(apiKeyId: ApiKeyId)
}





