package blocksize.assignment

import blocksize.assignment.crypt.Decryptor
import blocksize.assignment.crypt.Encryptor
import blocksize.assignment.data.*
import blocksize.assignment.model.*
import io.grpc.Status
import io.grpc.StatusRuntimeException


class ApikeyGrpcService(
  private val findByUserQuery: FindByUserQuery,
  private val getByIdQuery: GetByIdQuery,
  private val saveOperation: SaveApiKeyOperation,
  private val deleteOperation: DeleteApiKeyOperation,
  private val encryptor: Encryptor,
  private val decryptor: Decryptor
) :
  ApikeyServiceGrpcKt.ApikeyServiceCoroutineImplBase() {

  override suspend fun addApikey(request: Service.AddApikeyRequest): Service.AddApikeyResponse {
    //TODO: data validation requires more elegant solution
    require(request.userId.length < UserId.MAX_LENGTH) {"Max userId length is ${UserId.MAX_LENGTH}"}
    require(request.exchange.length < Exchange.MAX_LENGTH) {"Max exchange length is ${UserId.MAX_LENGTH}"}
    require(request.apikey.length < ApiKey.MAX_LENGTH) {"Max apikey length is ${UserId.MAX_LENGTH}"}
    require(request.secret.length < Secret.MAX_LENGTH) {"Max secret length is ${UserId.MAX_LENGTH}"}

    val encodedApiKey = EncodedApiKey(encryptor.encrypt(request.apikey))
    val encodedSecret = EncodedSecret(encryptor.encrypt(request.secret))

    val keyId = saveOperation.save(
        UserId(request.userId),
        Exchange(request.exchange),
        encodedApiKey,
        ApiKeyPreview.fromStr(request.apikey),
        encodedSecret
      )

    return Service.AddApikeyResponse.newBuilder().setApikeyId(keyId.value).build()
  }

  override suspend fun deleteApikey(request: Service.DeleteApikeyRequest): Service.DeleteApikeyResponse {
    deleteOperation.delete(ApiKeyId(request.apikeyId))
    return Service.DeleteApikeyResponse.newBuilder().build()
  }


  override suspend fun listApikeys(request: Service.ListApikeysRequest): Service.ListApikeysResponse {
    val response = Service.ListApikeysResponse.newBuilder()

    findByUserQuery.execute(UserId(request.userId)) {
      response.addApikeysBuilder()
        .setApikeyId(it.apiKeyId.value)
        .setExchange(it.exchange.value)
        .setApikeyPreview(it.apiKeyPreview.value)
    }

    return response.build()
  }

  override suspend fun getApikey(request: Service.GetApikeyRequest): Service.GetApikeyResponse {
    val key = getByIdQuery.execute(ApiKeyId(request.apikeyId)) ?: throw StatusRuntimeException(Status.NOT_FOUND)

    val decodedApiKey = key.apiKey.decode(decryptor)
    val decodedSecret = key.secret.decode(decryptor)

    val response = Service.GetApikeyResponse.newBuilder()
    response.apikeyBuilder
      .setApikey(decodedApiKey.value)
      .setSecret(decodedSecret.value)
      .apikeyDetailsBuilder
      .setApikeyId(key.apiKeyId.value)
      .setApikeyPreview(key.apiKeyPreview.value)
      .setExchange(key.exchange.value)

    return response.build()
  }
}
