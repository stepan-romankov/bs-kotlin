package blocksize.assignment.model

import blocksize.assignment.crypt.Decryptor

inline class UserId(val value: String) {
  companion object {
    const val MAX_LENGTH = 36
  }
}

inline class Exchange(val value: String) {
  companion object {
    const val MAX_LENGTH = 36
  }
}

inline class ApiKey(val value: String) {
  companion object {
    const val MAX_LENGTH = 4096
  }
}

inline class Secret(val value: String) {
  companion object {
    const val MAX_LENGTH = 4096
  }
}

inline class ApiKeyId(val value: String) {
  companion object {
    const val MAX_LENGTH = 36
  }
}

inline class ApiKeyPreview(val value: String) {
  companion object {
    const val MAX_LENGTH = 5

    fun fromStr(value: String): ApiKeyPreview {
      return ApiKeyPreview(value.take(PREVIEW_LENGTH))
    }

    private const val PREVIEW_LENGTH = 5
  }
}

inline class EncodedApiKey(val value: ByteArray) {
  fun decode(decryptor: Decryptor) = ApiKey(decryptor.decrypt(value))
}
inline class EncodedSecret(val value: ByteArray) {
  fun decode(decryptor: Decryptor) = Secret(decryptor.decrypt(value))
}

