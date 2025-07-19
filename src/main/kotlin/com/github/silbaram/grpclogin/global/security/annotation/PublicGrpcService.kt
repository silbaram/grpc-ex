package com.github.silbaram.grpclogin.global.security.annotation

@Target(AnnotationTarget.FUNCTION) // 함수(메소드)에 적용
@Retention(AnnotationRetention.RUNTIME) // 런타임에 참조 가능하도록 설정
annotation class PublicGrpcService
