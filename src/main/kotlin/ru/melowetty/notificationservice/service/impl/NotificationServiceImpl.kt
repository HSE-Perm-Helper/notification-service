package ru.melowetty.notificationservice.service.impl

import org.springframework.stereotype.Service
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.domain.entity.Notification
import ru.melowetty.notificationservice.domain.entity.NotificationRecord
import ru.melowetty.notificationservice.repository.NotificationRecordRepository
import ru.melowetty.notificationservice.repository.NotificationRepository
import ru.melowetty.notificationservice.service.NotificationService
import java.util.*

@Service
@Slf4j
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val notificationRecordRepository: NotificationRecordRepository
) : NotificationService {
    override fun addNotification(notification: Notification) {
        notificationRepository.save(notification)
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