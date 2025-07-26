package com.github.silbaram.grpclogin.error_message.service

import com.github.silbaram.grpclogin.error_message.port.inbound.ErrorMessageProvider
import com.github.silbaram.grpclogin.error_message.port.outbound.ErrorMessageOutport
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ErrorMessageService(
    private val errorMessageOutport: ErrorMessageOutport
): ErrorMessageProvider {

    private val log = LoggerFactory.getLogger(javaClass)

    @Cacheable(value = ["errorMessages"], key = "#errorCode + ':' + #lang")
    override suspend fun getMessage(errorCode: String, lang: String): String {
        log.warn("캐시 MISS! DB에서 조회합니다. errorCode={}, lang={}", errorCode, lang)

        // 캐시에 없으면 Port를 통해 DB에서 조회
        return errorMessageOutport.findBy(errorCode, lang)?.message ?: "오류 메시지를 찾을 수 없습니다. (코드: $errorCode)"
    }

}