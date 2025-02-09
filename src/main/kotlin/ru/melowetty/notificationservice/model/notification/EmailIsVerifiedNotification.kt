package ru.melowetty.notificationservice.model.notification

import ru.melowetty.notificationservice.annotation.KafkaNotification
import ru.melowetty.notificationservice.annotation.NotificationDestination
import ru.melowetty.notificationservice.annotation.telegram.TelegramNotification

@TelegramNotification("telegram/email_is_verified.txt")
@KafkaNotification(NotificationType.EMAIL_IS_VERIFIED)
data class EmailIsVerifiedNotification(
    @NotificationDestination
    val telegramId: Long
)
