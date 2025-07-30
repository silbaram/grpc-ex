package com.github.silbaram.grpclogin.global.security.gprc_interceptor

import com.github.silbaram.grpclogin.global.security.annotation.PublicGrpcService
import com.github.silbaram.grpclogin.member.login.application.service.JwtTokenProvider
import io.grpc.*
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.withContext
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import net.devh.boot.grpc.server.service.GrpcServiceDiscoverer
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.coroutines.CoroutineContext


@GrpcGlobalServerInterceptor
class JwtAuthInterceptor(
    private val jwtTokenProvider: JwtTokenProvider,
    private val grpcServiceDiscoverer: GrpcServiceDiscoverer // 서비스 탐색기 주입
) : ServerInterceptor {

    companion object {
        // 클라이언트와 약속된 메타데이터 키 (HTTP의 Authorization 헤더와 유사)
        private val AUTHORIZATION_HEADER_KEY: Metadata.Key<String> =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)

        val LANGUAGE_HEADER_KEY: Metadata.Key<String> =
            Metadata.Key.of("language-code", Metadata.ASCII_STRING_MARSHALLER)

        // Context Key 정의
        val LANGUAGE_CODE_KEY: Context.Key<String> = Context.key("language-code")

        // 코루틴에서 사용할 언어 컨텍스트 요소
        private val languageContextHolder = ThreadLocal<String>()

        // 현재 언어 설정 가져오기
        fun getCurrentLanguage(): String = languageContextHolder.get() ?: "en"

        // 언어 코드 코루틴 컨텍스트 요소
        fun languageContextElement(language: String): CoroutineContext.Element =
            languageContextHolder.asContextElement(language)
    }


    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata, // 요청 메타데이터
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        // 호출된 gRPC 메소드에 @PublicGrpcService 어노테이션이 있는지 확인
        if (isPublicGrpcMethod(call.methodDescriptor.fullMethodName.lowercase())) {
            val language = headers.get(LANGUAGE_HEADER_KEY) ?: "en"
            languageContextHolder.set(language)
            val contextWithLang = Context.current().withValue(LANGUAGE_CODE_KEY, language)
            return Contexts.interceptCall(contextWithLang, call, headers, next)
        }

        // 1. 메타데이터에서 'Authorization' 키로 토큰 추출
        val authHeader = headers.get(AUTHORIZATION_HEADER_KEY)

        // 2. 토큰 유효성 검사 (Bearer 접두사 확인 및 토큰 존재 여부)
        if (authHeader == null) {
            call.close(Status.UNAUTHENTICATED.withDescription("Authorization 헤더가 누락되었습니다."), headers)
            return object : ServerCall.Listener<ReqT>() {}
        }
        if (!authHeader.startsWith("Bearer ")) {
            call.close(Status.UNAUTHENTICATED.withDescription("Authorization 헤더 형식이 잘못되었습니다."), headers)
            return object : ServerCall.Listener<ReqT>() {}
        }

        val token = authHeader.substring(7) // "Bearer " 제거

        try {
            // 3. JWT 유효성 검증
            if (jwtTokenProvider.validateToken(token)) {
                // 4. 토큰이 유효하면 Spring Security 컨텍스트에 인증 정보 저장
                val authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication

                // 언어 설정 및 컨텍스트 전파
                val language = headers.get(LANGUAGE_HEADER_KEY) ?: "en"

                // ThreadLocal에도 저장해서 코루틴에서 접근 가능하게 함
                languageContextHolder.set(language)

                // gRPC Context에도 저장
                val contextWithLang = Context.current().withValue(LANGUAGE_CODE_KEY, language)

                // 다음 핸들러 호출
                return Contexts.interceptCall(contextWithLang, call, headers, next)
            } else {
                throw Status.UNAUTHENTICATED.withDescription("유효하지 않은 토큰입니다.").asRuntimeException()
            }
        } catch (e: Exception) {
            // 검증 중 예외 발생 시 요청 거부
            SecurityContextHolder.clearContext()
            call.close(Status.UNAUTHENTICATED.withDescription("인증 실패: ${e.message}"), headers)
            return object : ServerCall.Listener<ReqT>() {}
        }
    }

    // 캐싱된 Public API 메소드 목록
    private val publicApiMethods: Set<String> by lazy {
        grpcServiceDiscoverer.findGrpcServices().flatMap { serviceDefinition ->
            // 1. protoc가 생성한 실제 gRPC 서비스 이름을 가져옵니다.
            val grpcServiceName = serviceDefinition.definition.serviceDescriptor.name

            val kotlinClass = serviceDefinition.beanClazz

            // 2. 어노테이션이 붙은 코틀린 메소드를 찾습니다.
            kotlinClass.methods.filter { method ->
                method.isAnnotationPresent(PublicGrpcService::class.java) // 사용하는 어노테이션 클래스로 변경
            }.map { annotatedMethod ->
                // 3. "서비스명/메소드명" 형식으로 만들고, 비교를 위해 소문자로 통일합니다.
                "$grpcServiceName/${annotatedMethod.name}".lowercase()
            }
        }.toSet()
    }

    private fun isPublicGrpcMethod(fullMethodName: String): Boolean =
        publicApiMethods.contains(fullMethodName)
}

suspend fun getLanguage() = withContext(JwtAuthInterceptor.languageContextElement(JwtAuthInterceptor.getCurrentLanguage())) {
    // 여기서 JwtAuthInterceptor.getCurrentLanguage()를 통해 언어 설정에 접근
    val currentLanguage = JwtAuthInterceptor.getCurrentLanguage()
    // 비즈니스 로직...
}