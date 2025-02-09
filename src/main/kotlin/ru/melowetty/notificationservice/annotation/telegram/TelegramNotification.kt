package ru.melowetty.notificationservice.annotation.telegram

import ru.melowetty.notificationservice.annotation.Notification

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Notification(1)
annotation class TelegramNotification(
    val template: String
)
