package com.github.silbaram.grpclogin.error_message.port.outbound

import com.github.silbaram.grpclogin.global.entity.ErrorMessageEntity

interface ErrorMessageOutport {
    suspend fun findBy(errorCode: String, lang: String): ErrorMessageEntity?
}