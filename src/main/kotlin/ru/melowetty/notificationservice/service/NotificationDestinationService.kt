package ru.melowetty.notificationservice.service

import java.util.UUID
import org.springframework.stereotype.Service
import ru.melowetty.notificationservice.annotation.notification.Notification
import ru.melowetty.notificationservice.utils.ReflectionUtils
import ru.melowetty.notificationservice.utils.ReflectionUtils.Companion.extractInnerAnnotations

@Service
class NotificationDestinationService(
    private val userService: UserService,
) {
    fun getNotificationDestinations(notification: Any, userId: String): Map<Class<out Annotation>, Any> {
        val userInfo = userService.getUserInfo(UUID.fromString(userId))

        val annotations = notification::class.extractInnerAnnotations<Notification>()
        val destinations = annotations.associate { Pair(it.second.destinationAnnotation, it.first) }
        val fieldValues = ReflectionUtils.getFieldValuesByAnnotations(userInfo, destinations.keys.toList())

        val result = fieldValues.map { Pair(destinations[it.key], it.value) }
            .filter { it.first != null }
            .filter { it.second != null }
            .associate { it.first!!.annotationClass.java to it.second!! }

        return result
    }
}
