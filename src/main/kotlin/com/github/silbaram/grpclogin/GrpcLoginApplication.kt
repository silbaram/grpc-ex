package com.github.silbaram.grpclogin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrpcLoginApplication

fun main(args: Array<String>) {
    runApplication<GrpcLoginApplication>(*args)
}
