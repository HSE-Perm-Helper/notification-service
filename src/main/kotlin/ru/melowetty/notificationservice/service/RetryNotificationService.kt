package ru.melowetty.notificationservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RetryNotificationService(
    private val kafkaTemplate: KafkaTemplate<String, HashMap<String, Any?>>,
    private val objectMapper: ObjectMapper
) {
    @Value("\${spring.kafka.topic.next-gen-notifications}")
    private lateinit var notificationsTopic: String

    fun retryNotification(notification: Any, userId: UUID) {
        val notification: HashMap<String, Any?> = objectMapper.convertValue(notification)
        notification["userId"] = userId
        kafkaTemplate.send(notificationsTopic, notification)
    }
}