package ru.melowetty.notificationservice.model.notification.verification

import ru.melowetty.notificationservice.annotation.notification.ProcessableNotification
import ru.melowetty.notificationservice.annotation.notification.email.EmailNotification
import ru.melowetty.notificationservice.annotation.notification.telegram.TelegramNotification
import ru.melowetty.notificationservice.model.notification.base.NotificationType

@EmailNotification("email/email-verification")
@ProcessableNotification(NotificationType.EMAIL_VERIFICATION)
data class EmailVerificationNotification(
    val link: String,
)
