package com.github.silbaram.grpclogin.error_message.repository

import com.github.silbaram.grpclogin.global.entity.ErrorMessageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ErrorMessageRepository: JpaRepository<ErrorMessageEntity, Long> {
    fun findByErrorCodeAndLang(errorCode: String, lang: String): ErrorMessageEntity?
}