package com.github.silbaram.grpclogin.member.login.application.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * CustomReactiveUserDetailsService는 ReactiveUserDetailsService를 구현하여 사용자 정보를 비동기적으로 조회합니다.
 * spring-security에서 사용자 인증을 위해 사용됩니다.
 */
@Service
class CustomReactiveUserDetailsService : ReactiveUserDetailsService {

    // 실제로는 DB에서 사용자 정보를 조회해야 합니다.
    // 여기서는 이해를 돕기 위해 하드코딩된 사용자 목록을 사용합니다.
    private val users: Map<String, UserDetails>

    init {
        val passwordEncoder = BCryptPasswordEncoder()
        users = mapOf(
            "user" to User.builder()
                .username("user")
                .password(passwordEncoder.encode("password")) // 비밀번호는 반드시 암호화하여 저장
                .roles("USER")
                .build()
        )
    }

    /**
     * username을 기반으로 사용자 정보를 조회하는 메소드
     */
    override fun findByUsername(username: String?): Mono<UserDetails> {
        if (username == null) {
            return Mono.empty()
        }

        // Map에서 사용자 정보를 찾고, Mono 형태로 반환
        return Mono.justOrEmpty(users[username])
    }
}