package com.github.silbaram.grpclogin.error_message.adapter.inbound.grpc

import com.github.silbaram.grpclogin.error_message.port.inbound.ErrorMessageProvider
import com.github.silbaram.grpclogin.global.exception.GrpcBusinessException
import io.grpc.Metadata
import io.grpc.Status
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.runBlocking
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import org.slf4j.LoggerFactory

@GrpcAdvice
class GlobalGrpcExceptionAdvice(
    private val errorMessageProvider: ErrorMessageProvider
) {
    companion object {
        private val ERROR_CODE_KEY: Metadata.Key<String> =
            Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER)
        private val log = LoggerFactory.getLogger(GlobalGrpcExceptionAdvice::class.java)
    }

    @GrpcExceptionHandler(GrpcBusinessException::class)
    fun handleBusinessException(ex: GrpcBusinessException): StatusRuntimeException {
        val message =
            runBlocking {
                errorMessageProvider.getMessage("E401", "ko")
            }
        val metadata = Metadata()
        metadata.put(ERROR_CODE_KEY, ex.errorCode.name)
        log.error("gRPC Business Exception: ${ex.errorCode} - $message")
        return ex.status.withDescription(message).asRuntimeException(metadata)
    }

    @GrpcExceptionHandler(Exception::class)
    fun handleException(ex: Exception): StatusRuntimeException {
        log.error("gRPC Unknown Exception", ex)
        return Status.INTERNAL.withDescription("Internal server error").asRuntimeException()
    }
}