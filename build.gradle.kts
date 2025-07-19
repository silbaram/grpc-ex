import com.google.protobuf.gradle.*

plugins {
    // Spring Boot + Dependency Management
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"

    // Kotlin
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"

    // Protobuf & gRPC
    id("com.google.protobuf") version "0.9.5"
}

group = "com.github.silbaram"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra.apply {
    // 버전 관리를 한곳에서
    set("grpcVersion", "1.73.0")
    set("grpcKotlinVersion", "1.4.3")
    set("protobufVersion", "4.31.1")
    set("protobufKotlinVersion", "4.31.1")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // gRPC + Spring Boot Starter
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")

    // Protobuf & gRPC
    implementation("com.google.protobuf:protobuf-java:${property("protobufVersion")}")
    implementation("com.google.protobuf:protobuf-kotlin:${property("protobufKotlinVersion")}")
    implementation("io.grpc:grpc-netty-shaded:${property("grpcVersion")}")
    implementation("io.grpc:grpc-protobuf:${property("grpcVersion")}")
    implementation("io.grpc:grpc-stub:${property("grpcVersion")}")
    implementation("io.grpc:grpc-kotlin-stub:${property("grpcKotlinVersion")}")

    implementation("io.grpc:grpc-inprocess:${property("grpcVersion")}")
    implementation("io.grpc:grpc-services:${property("grpcVersion")}")

    // JWT 라이브러리
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
    protoc {
        // protoc 컴파일러 버전 지정
        artifact = "com.google.protobuf:protoc:${property("protobufVersion")}"
    }
    plugins {
        // gRPC Java 플러그인
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${property("grpcVersion")}"
        }
        // gRPC Kotlin 플러그인
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${property("grpcKotlinVersion")}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")     // Java 스텁 생성
                id("grpckt")   // Kotlin 스텁 생성
            }
            task.builtins {
                id("kotlin")   // Kotlin 메시지 클래스 생성 (Java 메시지도 함께 생성됨) :contentReference[oaicite:1]{index=1}
            }
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
