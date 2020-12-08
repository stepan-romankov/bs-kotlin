package blocksize.assignment.data.sql

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random.Default.nextInt

abstract class DbTest {
  protected lateinit var tm: TransactionManager

  @BeforeEach
  open fun setUp() {
    val db = Database.connect("jdbc:h2:mem:${nextInt()};DB_CLOSE_DELAY=-1")
    transaction(db) {
      SchemaUtils.createMissingTablesAndColumns(ApiKeysTable)
    }
    tm = TransactionManager(db)
  }
}
