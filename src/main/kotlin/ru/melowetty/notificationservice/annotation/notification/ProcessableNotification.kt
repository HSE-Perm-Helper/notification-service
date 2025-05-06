package ru.melowetty.notificationservice.annotation.notification

import ru.melowetty.notificationservice.model.notification.base.NotificationType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProcessableNotification(
    val notificationType: NotificationType,
)
