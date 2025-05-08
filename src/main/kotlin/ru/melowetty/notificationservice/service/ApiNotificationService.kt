package ru.melowetty.notificationservice.service

import ru.melowetty.notificationservice.model.ApiNotification
import java.util.UUID

interface ApiNotificationService {
    fun addNotification(notification: ApiNotification)

    fun getAllNotifications(): List<ApiNotification>

    fun deleteNotifications(ids: List<UUID>)
}
