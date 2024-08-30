package ru.melowetty.notificationservice.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class NotificationRecord(
    @Id
    val id: UUID,
    val date: LocalDateTime,
    val notificationType: String,
    val payload: Any
)
