package ru.melowetty.notificationservice.annotation.notification.telegram

import ru.melowetty.notificationservice.annotation.notification.Notification
import ru.melowetty.notificationservice.annotation.Priority

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Notification(TelegramDestination::class)
annotation class TelegramNotification(
    val template: String,
    val priority: Priority = Priority(1)
)
