package blocksize.assignment.data.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.Driver

fun createDatasource(jdbcUrl: String, username:String, password: String): HikariDataSource {

  val config = HikariConfig()
  config.driverClassName = Driver::class.java.name
  config.jdbcUrl = jdbcUrl
  config.username = username
  config.password = password
  config.minimumIdle = 3
  config.maximumPoolSize = 10
  config.isAutoCommit = false
  config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
  config.validate()
  return HikariDataSource(config)
}
