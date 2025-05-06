package ru.melowetty.notificationservice.model

import java.time.LocalDateTime
import java.util.UUID

data class ApiNotification(
    val id: UUID = UUID.randomUUID(),
    val date: LocalDateTime = LocalDateTime.now(),
    val notificationType: String,
    val payload: Any,
)
