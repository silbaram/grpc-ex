package com.github.silbaram.grpclogin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class GrpcLoginApplication

fun main(args: Array<String>) {
    runApplication<GrpcLoginApplication>(*args)
}
