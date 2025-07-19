package com.github.silbaram.grpclogin.member.login.adapter.inbound.rpc

import com.example.grpc.auth.AuthServiceGrpcKt
import com.example.grpc.auth.LoginRequest
import com.example.grpc.auth.LoginResponse
import com.github.silbaram.grpclogin.global.security.annotation.PublicGrpcService
import com.github.silbaram.grpclogin.member.login.application.service.JwtTokenProvider
import io.grpc.Status
import kotlinx.coroutines.reactive.awaitSingle
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException

@GrpcService
class AuthServiceImpl(
    private val reactiveAuthenticationManager: ReactiveAuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) : AuthServiceGrpcKt.AuthServiceCoroutineImplBase() {

    @PublicGrpcService
    override suspend fun login(request: LoginRequest): LoginResponse {
        try {
            request.validate()
            
            val authenticationToken = UsernamePasswordAuthenticationToken(request.username, request.password)

            // 1. Mono<Authentication>을 .awaitSingle()을 통해 Authentication 객체로 직접 변환
            val authenticatedAuthentication = reactiveAuthenticationManager.authenticate(authenticationToken).awaitSingle()

            // 2. 인증 성공 시 JWT 생성
            val accessToken = jwtTokenProvider.createAccessToken(authenticatedAuthentication)
            val refreshToken = jwtTokenProvider.createRefreshToken(authenticatedAuthentication)

            // 3. LoginResponse를 직접 return
            return LoginResponse.newBuilder()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .build()

        } catch (e: AuthenticationException) {
            // 4. 인증 예외 발생 시 Status 예외를 throw
            throw Status.UNAUTHENTICATED
                .withDescription("인증에 실패했습니다: ${e.message}")
                .asException()
        } catch (e: Exception) {
            // 그 외 예외 처리
            throw Status.INTERNAL
                .withDescription("예상치 못한 오류가 발생했습니다: ${e.message}")
                .asException()
        }
    }

    private fun LoginRequest.validate() {
        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            throw Status.INVALID_ARGUMENT
                .withDescription("username과 password는 필수입니다.")
                .asException()
        }
    }
}