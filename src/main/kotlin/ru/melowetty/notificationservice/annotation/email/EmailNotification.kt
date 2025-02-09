package ru.melowetty.notificationservice.annotation.email

import ru.melowetty.notificationservice.annotation.Notification

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Notification(2)
annotation class EmailNotification(
    val template: String
)
