package ru.melowetty.notificationservice.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.melowetty.notificationservice.controller.request.NotificationData
import ru.melowetty.notificationservice.domain.entity.Notification
import ru.melowetty.notificationservice.service.NotificationService

@RestController
@RequestMapping("notifications")
class NotificationController(
    private val notificationService: NotificationService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getNotifications(): List<Notification> {
        return notificationService.getAllNotifications()
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteNotifications(
        @RequestBody
        notifications: List<NotificationData>) {
        notificationService.deleteNotifications(notifications.map { it.id })
    }
}