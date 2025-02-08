package ru.melowetty.notificationservice.service.impl

import org.springframework.stereotype.Service
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.model.Notification
import ru.melowetty.notificationservice.service.NotificationService
import java.util.*
import kotlin.collections.LinkedHashSet

@Service
@Slf4j
class NotificationServiceImpl: NotificationService {
    private val notifications: LinkedHashSet<Notification> = linkedSetOf()

    override fun addNotification(notification: Notification) {
        notifications.add(notification)
    }

    override fun getAllNotifications(): List<Notification> {
        return notifications.toList()
    }

    override fun deleteNotifications(ids: List<UUID>) {
        val setOfIds = ids.toSet()
        notifications.removeAll { setOfIds.contains(it.id) }
    }
}