package ru.melowetty.notificationservice.service.impl

import java.util.UUID
import kotlin.collections.LinkedHashSet
import org.springframework.stereotype.Service
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.model.ApiNotification
import ru.melowetty.notificationservice.service.ApiNotificationService

@Service
@Slf4j
class ApiNotificationServiceImpl : ApiNotificationService {
  private val notifications: LinkedHashSet<ApiNotification> = linkedSetOf()

  override fun addNotification(notification: ApiNotification) {
    notifications.add(notification)
  }

  override fun getAllNotifications(): List<ApiNotification> {
    return notifications.toList()
  }

  override fun deleteNotifications(ids: List<UUID>) {
    val setOfIds = ids.toSet()
    notifications.removeAll { setOfIds.contains(it.id) }
  }
}
