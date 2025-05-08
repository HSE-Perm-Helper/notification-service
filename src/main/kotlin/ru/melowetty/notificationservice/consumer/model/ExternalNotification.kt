package ru.melowetty.notificationservice.consumer.model

data class ExternalNotification(
    val notificationType: String,
    val payload: Any,
)
