package ru.melowetty.notificationservice.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.consumer.model.ExternalNotification
import ru.melowetty.notificationservice.model.ApiNotification
import ru.melowetty.notificationservice.service.ApiNotificationService
import ru.melowetty.notificationservice.utils.LoggingUtils

@Component
class ExternalNotificationConsumer(
    private val notificationService: ApiNotificationService,
) {
    @KafkaListener(
        topics = ["\${spring.kafka.topic.notifications}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun consumeNewNotification(notification: ExternalNotification) {
        LoggingUtils.executeWithRequestIdContext {
            val newNotify =
                ApiNotification(
                    notificationType = notification.notificationType,
                    payload = notification.payload,
                )

            notificationService.addNotification(newNotify)
        }
    }
}
