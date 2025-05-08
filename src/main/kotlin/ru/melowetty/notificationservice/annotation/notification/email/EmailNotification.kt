package ru.melowetty.notificationservice.annotation.notification.email

import ru.melowetty.notificationservice.annotation.notification.Notification
import ru.melowetty.notificationservice.annotation.Priority

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Notification(EmailDestination::class)
annotation class EmailNotification(
    val template: String,
    val priority: Priority = Priority(1)
)
