package blocksize.assignment.data.sql

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class TransactionManager(val db: Database) {
  suspend fun <T> tx(block: suspend Transaction.() -> T): T {
    return newSuspendedTransaction(Dispatchers.IO, db) {
        block()
    }
  }
}

