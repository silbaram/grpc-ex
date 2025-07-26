package com.github.silbaram.grpclogin.error_message.port.inbound

interface ErrorMessageProvider {
    suspend fun getMessage(errorCode: String, lang: String = "ko"): String
}