package ru.melowetty.notificationservice.annotation.telegram

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TelegramNotification(
    val template: String
)
