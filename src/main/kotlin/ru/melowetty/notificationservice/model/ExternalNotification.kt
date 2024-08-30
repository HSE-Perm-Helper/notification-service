package ru.melowetty.notificationservice.model

data class ExternalNotification(
    val notificationType: String,
    val payload: Any
)
