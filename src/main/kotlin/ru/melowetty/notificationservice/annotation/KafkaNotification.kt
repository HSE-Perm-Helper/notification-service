package ru.melowetty.notificationservice.annotation

import ru.melowetty.notificationservice.model.notification.NotificationType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KafkaNotification(
    val notificationType: NotificationType
)
