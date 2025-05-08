package ru.melowetty.notificationservice.model.user

import ru.melowetty.notificationservice.annotation.notification.email.EmailDestination
import ru.melowetty.notificationservice.annotation.notification.telegram.TelegramDestination

data class UserInfo(
    @EmailDestination val email: String?,
    @TelegramDestination val telegramId: Long?,
)
