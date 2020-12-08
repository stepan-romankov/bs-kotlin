package blocksize.assignment

import blocksize.assignment.crypt.AESCrypt
import blocksize.assignment.data.sql.*
import io.grpc.BindableService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
  val tm = transactionManager()
  val cryptor = cryptor()
  val apikeyGrpcService = services(tm, cryptor)
  server(apikeyGrpcService)
}

private fun server(apikeyGrpcService: List<BindableService>) {
  val server_port = System.getenv("SERVER_PORT")?.toInt() ?: 50051
  val server = Server(server_port, apikeyGrpcService)
  server.start()
  server.blockUntilShutdown()
}

private fun services(tm: TransactionManager, cryptor: AESCrypt): List<BindableService> {
  val apikeyGrpcService = ApikeyGrpcService(
    DbFindByUserQuery(tm),
    DbGetByIdQuery(tm),
    DbSaveApiKeyOperation(tm),
    DbDeleteApiKeyOperation(tm),
    cryptor,
    cryptor
  )
  return listOf<BindableService>(apikeyGrpcService)
}

private fun cryptor(): AESCrypt {
  val secret = System.getenv("SECRET") ?: "bl0cks!ze@#$%#%@#$^*Wrew" // 24 random symbols
  val cryptor = AESCrypt(secret)
  return cryptor
}

private fun transactionManager(): TransactionManager {
  val postgresHost = System.getenv("POSTGRES_HOST") ?: "localhost"
  val postgresPort = System.getenv("POSTGRES_PORT")?.toInt() ?: 5432
  val postgresDb = System.getenv("POSTGRES_DB") ?: "blocksize"
  val postgresUser = System.getenv("POSTGRES_USER") ?: "postgres"
  val postgresPassword = System.getenv("POSTGRES_password") ?: "test"

  val jdbcUrl = "jdbc:postgresql://$postgresHost:$postgresPort/$postgresDb"
  val db = Database.connect(createDatasource(jdbcUrl, postgresUser, postgresPassword))
  val tm = TransactionManager(db)

  transaction(db) {
    SchemaUtils.createMissingTablesAndColumns(ApiKeysTable)
  }
  return tm
}



