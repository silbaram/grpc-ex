package com.github.silbaram.grpclogin.error_message.port.outbound

import com.github.silbaram.grpclogin.error_message.repository.ErrorMessageRepository
import com.github.silbaram.grpclogin.global.entity.ErrorMessageEntity
import org.springframework.stereotype.Component

@Component
class ErrorMessageAdapter(
    private val errorMessageRepository: ErrorMessageRepository
): ErrorMessageOutport {

    override suspend fun findBy(errorCode: String, lang: String): ErrorMessageEntity? =
        errorMessageRepository.findByErrorCodeAndLang(errorCode, lang)
}