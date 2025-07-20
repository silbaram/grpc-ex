package com.github.silbaram.grpclogin.global.exception

import common.errors.code.ErrorCode
import io.grpc.Status

class GrpcBusinessException(
    val errorCode: ErrorCode,
    override val message: String,
    val status: Status
) : RuntimeException(message)
