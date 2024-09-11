package ru.melowetty.notificationservice.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.domain.entity.Notification
import ru.melowetty.notificationservice.model.ExternalNotification
import ru.melowetty.notificationservice.service.NotificationService

@Component
class ExternalNotificationConsumer(
    private val notificationService: NotificationService
) {
    @KafkaListener(topics = ["new-notification"], groupId = "notification-service")
    fun consumeNewNotification(notification: ExternalNotification) {
        log.info("Received $notification")

        val newNotify = Notification(
            notificationType = notification.notificationType,
            payload = notification.payload
        )

        notificationService.addNotification(newNotify)
    }
}