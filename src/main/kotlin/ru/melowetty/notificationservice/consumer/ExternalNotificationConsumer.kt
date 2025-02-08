package ru.melowetty.notificationservice.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.model.Notification
import ru.melowetty.notificationservice.model.ExternalNotification
import ru.melowetty.notificationservice.service.NotificationService

@Component
class ExternalNotificationConsumer(
    private val notificationService: NotificationService
) {
    @KafkaListener(
        topics = ["\${spring.kafka.topic.notifications}"],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun consumeNewNotification(notification: ExternalNotification) {
        val newNotify = Notification(
            notificationType = notification.notificationType,
            payload = notification.payload
        )

        notificationService.addNotification(newNotify)
    }
}