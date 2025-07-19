package com.github.silbaram.grpclogin.member.login.application.service

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

const val AUTHORITIES_KEY = "auth"

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-token-expiration-ms}") private val accessTokenExpirationMs: Long,
    @Value("\${jwt.refresh-token-expiration-ms}") private val refreshTokenExpirationMs: Long,
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val key: SecretKey

    // `init` 블록: 생성자 호출 이후, 의존성 주입이 완료된 후 실행됨
    // Base64로 인코딩된 secretKey를 디코딩하여 Key 객체로 생성
    init {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    /**
     * Access Token 생성
     */
    fun createAccessToken(authentication: Authentication): String {
        return createToken(authentication, accessTokenExpirationMs)
    }

    /**
     * Refresh Token 생성
     */
    fun createRefreshToken(authentication: Authentication): String {
        return createToken(authentication, refreshTokenExpirationMs)
    }

    private fun createToken(authentication: Authentication, validityMs: Long): String {
        val now = Date()
        val validity = Date(now.time + validityMs)

        // 권한 정보를 문자열로 변환
        val authorities = authentication.authorities.joinToString(",") { it.authority }

        return Jwts.builder()
            .subject(authentication.name) // 사용자 ID (username)
            .claim(AUTHORITIES_KEY, authorities) // 권한 정보 저장
            .signWith(key, SignatureAlgorithm.HS512)
            .issuedAt(now) // 토큰 발행 시간
            .expiration(validity) // 토큰 만료 시간
            .compact()
    }

    /**
     * 토큰에서 인증(Authentication) 정보 조회
     */
    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)

        // 클레임에서 권한 정보 추출
        val authorities: Collection<GrantedAuthority> =
            claims[AUTHORITIES_KEY].toString().split(",")
                .map { SimpleGrantedAuthority(it) }
                .toList()

        // UserDetails 객체 생성
        val principal: UserDetails = User(claims.subject, "", authorities)

        // Authentication 객체 반환
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    /**
     * 토큰 유효성 검증
     */
    fun validateToken(token: String): Boolean {
        try {
            parseClaims(token)
            return true
        } catch (e: SecurityException) {
            log.info("잘못된 JWT 서명입니다.")
        } catch (e: MalformedJwtException) {
            log.info("잘못된 JWT 서명입니다.")
        } catch (e: ExpiredJwtException) {
            log.info("만료된 JWT 토큰입니다.")
        } catch (e: UnsupportedJwtException) {
            log.info("지원되지 않는 JWT 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            log.info("JWT 토큰이 잘못되었습니다.")
        }
        return false
    }

    /**
     * 토큰을 파싱하여 클레임(정보) 추출
     */
    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .payload
        } catch (e: ExpiredJwtException) {
            // 만료된 토큰이더라도 정보를 꺼내야 할 때가 있으므로 claims 반환
            e.claims
        }
    }
}