package ru.melowetty.notificationservice.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

@Document
data class Notification (
    @Id
    val id: UUID = UUID.randomUUID(),
    val date: LocalDateTime = LocalDateTime.now(),
    val notificationType: String,
    val payload: Any
)