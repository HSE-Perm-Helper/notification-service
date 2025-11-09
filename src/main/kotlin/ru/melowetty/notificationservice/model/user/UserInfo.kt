package ru.melowetty.notificationservice.model.user

import ru.melowetty.notificationservice.annotation.notification.email.EmailDestination
import ru.melowetty.notificationservice.annotation.notification.telegram.TelegramDestination
import java.util.UUID

data class UserInfo(
    val id: UUID,
    @EmailDestination val email: String?,
    @TelegramDestination val telegramId: Long?,
)
