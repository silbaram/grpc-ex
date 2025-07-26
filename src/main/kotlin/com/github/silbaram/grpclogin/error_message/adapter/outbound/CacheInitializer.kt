package com.github.silbaram.grpclogin.error_message.adapter.outbound

import com.github.silbaram.grpclogin.error_message.repository.ErrorMessageRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CacheInitializer(
    private val cacheManager: CacheManager,
    private val errorMessageRepository: ErrorMessageRepository
) {

    @EventListener(ApplicationReadyEvent::class)
    fun initializeCache() {
        log.info("오류 메시지 캐시 초기화 시작...")
        val errorMessageCache = cacheManager.getCache("errorMessages") ?: return

        val entities = errorMessageRepository.findAll()
        entities.forEach { domainModel ->
            val cacheKey = "${domainModel.errorCode}:${domainModel.lang}"
            errorMessageCache.put(cacheKey, domainModel.message)
            log.info("캐시 적재: $cacheKey -> ${domainModel.message}")
        }

        log.info("캐시 적재 완료.")
    }

    private val log = LoggerFactory.getLogger(javaClass)
}