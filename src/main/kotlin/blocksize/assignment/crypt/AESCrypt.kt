package blocksize.assignment.crypt

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AESCrypt(password: String) : Encryptor, Decryptor {
  private val keySpec : SecretKeySpec

  init {
    require(password.length == 24) { "AESCrypt requires 24 symbols secret ky"  }
    keySpec = SecretKeySpec(password.toByteArray(),"AES")
  }

  /**
   * aes encryption
   */
  override fun encrypt(value: String): ByteArray {
    //1. Create a cipher object
    val cipher = Cipher.getInstance("AES")
    //2. Initialize cipher
    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
    //3. Encryption and decryption
    val encrypt = cipher.doFinal(value.toByteArray());
    return encrypt
  }

  /**
   * aes decryption
   */
  override fun decrypt(value: ByteArray): String {
    //1. Create a cipher object
    val cipher = Cipher.getInstance("AES")
    //2. Initialize cipher
    cipher.init(Cipher.DECRYPT_MODE, keySpec)
    //3. Encryption and decryption
    val decrypt = cipher.doFinal(value)
    return String(decrypt)
  }

}
