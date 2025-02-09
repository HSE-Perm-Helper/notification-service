package ru.melowetty.notificationservice.model.notification

import ru.melowetty.notificationservice.annotation.KafkaNotification
import ru.melowetty.notificationservice.annotation.telegram.TelegramId
import ru.melowetty.notificationservice.annotation.telegram.TelegramNotification

@TelegramNotification("email_is_verified")
@KafkaNotification(NotificationType.EMAIL_IS_VERIFIED)
data class EmailIsVerifiedNotification(
    @TelegramId
    val telegramId: Long
)
