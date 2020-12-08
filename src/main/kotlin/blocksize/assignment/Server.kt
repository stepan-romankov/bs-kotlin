package blocksize.assignment

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory
import java.io.IOException

class Server(port: Int, services: List<BindableService>) {
  val log = LoggerFactory.getLogger(this.javaClass)

  val server: Server

  init {
    val serverBuilder = ServerBuilder.forPort(port)
    services.forEach { serverBuilder.addService(it) }
    serverBuilder.intercept(ExceptionInterceptor())
    server = serverBuilder.build()
  }

  fun start() {
    try {
      server.start()
    } catch (e : IOException) {
      log.error("Failed to start GRPC server on port ${server.port}")
      throw e;
    }

    log.info("Server started, listening on ${server.port}")
    Runtime.getRuntime().addShutdownHook(
      Thread {
        log.info("*** shutting down gRPC server since JVM is shutting down")
        this@Server.stop()
        log.info("*** server shut down")
      }
    )
  }

  fun stop() {
    if (server.isShutdown) {
      return
    }

    server.shutdown()
  }

  fun blockUntilShutdown() {
    server.awaitTermination()
  }

}
