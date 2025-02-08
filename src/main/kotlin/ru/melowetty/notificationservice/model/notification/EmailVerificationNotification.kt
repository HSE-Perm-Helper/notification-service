package ru.melowetty.notificationservice.model.notification

import ru.melowetty.notificationservice.annotation.KafkaNotification

@KafkaNotification(NotificationType.EMAIL_VERIFICATION)
data class EmailVerificationNotification(
    val email: String,
    val link: String
)
