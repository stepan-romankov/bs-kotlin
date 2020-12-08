package blocksize.assignment.crypt

interface Encryptor {
  fun encrypt(value: String) : ByteArray
}
