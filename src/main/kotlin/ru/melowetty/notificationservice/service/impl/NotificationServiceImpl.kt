package ru.melowetty.notificationservice.service.impl

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.domain.entity.Notification
import ru.melowetty.notificationservice.domain.entity.NotificationRecord
import ru.melowetty.notificationservice.model.ExternalNotification
import ru.melowetty.notificationservice.repository.NotificationRecordRepository
import ru.melowetty.notificationservice.repository.NotificationRepository
import ru.melowetty.notificationservice.service.NotificationService
import java.util.*

@Service
@Slf4j
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val notificationRecordRepository: NotificationRecordRepository
): NotificationService {

    @KafkaListener(topics = ["new-notification"], groupId = "notification-service")
    fun listenNewNotification(notification: ExternalNotification) {
        log.info("Received $notification")

        val newNotify = Notification(
            notificationType = notification.notificationType,
            payload = notification.payload
        )

        notificationRepository.save(newNotify)
    }

    override fun getAllNotifications(): List<Notification> {
        return notificationRepository.findAll()
    }

    override fun deleteNotifications(ids: List<UUID>) {
        val existsNotifications = ids.filter {
            notificationRepository.existsById(it)
        }.map {
            notificationRepository.findById(it).get()
        }

        notificationRecordRepository.saveAll(existsNotifications.map {
            NotificationRecord(
                id = it.id,
                date = it.date,
                notificationType = it.notificationType,
                payload = it.payload,
            )
        })
        
        notificationRepository.deleteAllById(ids)
    }
}