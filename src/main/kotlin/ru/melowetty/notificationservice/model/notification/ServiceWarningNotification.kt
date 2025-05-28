package ru.melowetty.notificationservice.model.notification

import ru.melowetty.notificationservice.annotation.notification.ProcessableNotification
import ru.melowetty.notificationservice.annotation.notification.telegram.TelegramNotification
import ru.melowetty.notificationservice.model.notification.base.NotificationType

@TelegramNotification("telegram/service_warning.txt")
@ProcessableNotification(NotificationType.SERVICE_WARNING)
data class ServiceWarningNotification(
    val message: String,
)
