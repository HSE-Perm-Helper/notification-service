plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("se.solrike.sonarlint") version "1.0.0-beta.9"
}

group = "ru.melowetty"
version = "latest"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.telegram:telegrambots-client:8.0.0")
    implementation("org.jsoup:jsoup:1.18.3")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    reports {
        junitXml.required.set(true)
    }
}

tasks.jar {
    archiveFileName.set("notification-service.jar")
}

tasks.bootJar {
    archiveFileName.set("notification-service-standalone.jar")
}

sonarlint {
    maxIssues = 0
    dependencies {
        sonarlintPlugins("org.sonarsource.kotlin:sonar-kotlin-plugin:2.13.0.2116")
    }
}

tasks.sonarlintMain {
    reports {
        create("xml") {
            enabled.set(true)
        }
    }
}

tasks.sonarlintTest {
    ignoreFailures.set(true)
}