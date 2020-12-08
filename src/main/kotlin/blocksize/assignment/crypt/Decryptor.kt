package blocksize.assignment.crypt

interface Decryptor {
  fun decrypt(value: ByteArray) : String
}
