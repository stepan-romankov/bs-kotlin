package blocksize.assignment

import blocksize.assignment.data.DuplicateEntityException
import io.grpc.*
import org.slf4j.LoggerFactory

class ExceptionInterceptor : ServerInterceptor {

  /**
   * When closing a gRPC call, extract any error status information to top-level fields. Also
   * log the cause of errors.
   */
  private class ExceptionTranslatingServerCall<ReqT, RespT>(
    delegate: ServerCall<ReqT, RespT>
  ) : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(delegate) {

    private val logger = LoggerFactory.getLogger(ExceptionInterceptor::class.java)

    override fun close(status: Status, trailers: Metadata) {
      if (status.isOk) {
        return super.close(status, trailers)
      }
      val cause = status.cause
      var newStatus = status

      if (status.code == Status.Code.UNKNOWN) {
        val translatedStatus = when (cause) {
          is IllegalArgumentException -> Status.INVALID_ARGUMENT
          is IllegalStateException -> Status.FAILED_PRECONDITION
          is DuplicateEntityException -> Status.ALREADY_EXISTS
          is AssertionError -> Status.INVALID_ARGUMENT
          else -> Status.UNKNOWN
        }
        newStatus = translatedStatus.withDescription(cause?.message).withCause(cause)
      }

      when (newStatus.code) {
        Status.Code.UNKNOWN -> logger.error("Error handling gRPC endpoint.", cause)
        else -> logger.debug("Invalid request: {}", cause?.message)
      }

      super.close(newStatus, trailers)
    }
  }

  override fun <ReqT : Any, RespT : Any> interceptCall(
    call: ServerCall<ReqT, RespT>,
    headers: Metadata,
    next: ServerCallHandler<ReqT, RespT>
  ): ServerCall.Listener<ReqT> {
    return next.startCall(ExceptionTranslatingServerCall(call), headers)
  }
}
