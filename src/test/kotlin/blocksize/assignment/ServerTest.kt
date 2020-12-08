package blocksize.assignment

import blocksize.assignment.data.sql.DbTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ServerTest : DbTest() {

    @Test
    fun start() {
      val server = Server(0, emptyList())
      server.start()
      server.stop()
    }
}
