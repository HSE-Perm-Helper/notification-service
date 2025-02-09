package ru.melowetty.notificationservice.model.notification

import ru.melowetty.notificationservice.annotation.KafkaNotification
import ru.melowetty.notificationservice.annotation.NotificationDestination
import ru.melowetty.notificationservice.annotation.email.EmailNotification

@EmailNotification("email-verification")
@KafkaNotification(NotificationType.EMAIL_VERIFICATION)
data class EmailVerificationNotification(
    @NotificationDestination
    val email: String,
    val link: String
)
