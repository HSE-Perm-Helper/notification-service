package ru.melowetty.notificationservice.service

import ru.melowetty.notificationservice.model.Notification
import java.util.UUID

interface NotificationService {
    fun addNotification(notification: Notification)
    fun getAllNotifications(): List<Notification>
    fun deleteNotifications(ids: List<UUID>)
}