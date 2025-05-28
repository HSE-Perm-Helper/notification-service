package ru.melowetty.notificationservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfiguration {
    @Value("\${spring.user-service.base-url}")
    private lateinit var userServiceBaseUrl: String

    @Bean
    fun userServiceRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl(userServiceBaseUrl)
            .build()
    }
}