package com.github.silbaram.grpclogin.global.entity.util

import jakarta.persistence.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

inline fun <reified T: Any> columnName(prop: KProperty1<T, *>): String {
    val field = prop.javaField ?: return prop.name
    val ann = field.getAnnotation(Column::class.java)
    return ann?.name?.takeIf { it.isNotBlank() } ?: prop.name
}
