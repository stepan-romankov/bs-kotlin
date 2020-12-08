package blocksize.assignment.data.sql

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.postgresql.util.PSQLException
import java.sql.SQLIntegrityConstraintViolationException

private const val DUPLICATE_KEY_SQL_STATE = "23505"
val ExposedSQLException.duplicateKey: Boolean
  get() = when (val cause = this.cause) {
      is PSQLException -> cause.sqlState == DUPLICATE_KEY_SQL_STATE
      is SQLIntegrityConstraintViolationException -> cause.sqlState == DUPLICATE_KEY_SQL_STATE
      else -> false
    }

