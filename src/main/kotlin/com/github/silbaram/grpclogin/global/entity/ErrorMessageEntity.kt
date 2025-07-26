package com.github.silbaram.grpclogin.global.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ERROR_MESSAGE")
data class ErrorMessageEntity(
    @Id val id: Long? = null,
    val errorCode: String? = null,
    val lang: String? = null,
    val message: String? = null
)
