package ru.melowetty.notificationservice.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import ru.melowetty.notificationservice.controller.request.NotificationData
import ru.melowetty.notificationservice.model.ApiNotification
import ru.melowetty.notificationservice.service.ApiNotificationService

@RestController
@RequestMapping("notifications")
class NotificationController(private val notificationService: ApiNotificationService) {
  @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
  fun getNotifications(): List<ApiNotification> {
    return notificationService.getAllNotifications()
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun deleteNotifications(@RequestBody notifications: List<NotificationData>) {
    notificationService.deleteNotifications(notifications.map { it.id })
  }
}
