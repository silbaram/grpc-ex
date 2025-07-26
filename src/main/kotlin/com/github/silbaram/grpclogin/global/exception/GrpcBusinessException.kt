package com.github.silbaram.grpclogin.global.exception

import common.errors.code.ErrorCode
import io.grpc.Status

class GrpcBusinessException(
    val errorCode: ErrorCode,
    val status: Status
) : RuntimeException()
