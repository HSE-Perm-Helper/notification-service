package ru.melowetty.notificationservice.processor

import org.springframework.core.ResolvableType
import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.Priority
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.annotation.notification.Notification
import ru.melowetty.notificationservice.processor.base.NotificationProcessor
import ru.melowetty.notificationservice.service.NotificationDestinationService
import ru.melowetty.notificationservice.service.RetryNotificationService
import ru.melowetty.notificationservice.utils.ReflectionUtils
import java.util.UUID

@Component
@Slf4j
final class CommonNotificationProcessor(
    private val processors: List<NotificationProcessor<*, *>>,
    private val notificationDestinationService: NotificationDestinationService,
    private val retryNotificationService: RetryNotificationService,
) {
    companion object {
        private const val PRIORITY_FIELD = "priority"

        private val targetAnnotation = Notification::class.java

        fun getOrderedNotificationAnnotations(
            notification: Any,
            notificationAnnotation: Class<out Annotation>,
            priorityField: String
        ): List<Annotation> {
            return notification::class
                .java
                .annotations
                .filter { it.annotationClass.java.isAnnotationPresent(notificationAnnotation) }
                .sortedBy {
                    val priority = ReflectionUtils.getAnnotationFieldValue(notification::class, it, priorityField) as? Priority

                    priority?.value ?: Integer.MAX_VALUE
                }
        }
    }

    private val processorByAnnotation = getProcessorByAnnotationMap()

    private final fun getProcessorByAnnotationMap(): Map<Class<out Annotation>, NotificationProcessor<*, *>> =
        processors.associateBy { processor ->
            val type =
                ResolvableType.forClass(processor.javaClass).`as`(NotificationProcessor::class.java)

            val genericType =
                type.getGeneric(0).resolve()
                    ?: throw RuntimeException(
                        "Не все процессоры верно настроены, запуск невозможен: ${processor.javaClass.simpleName}",
                    )

            if (!Annotation::class.java.isAssignableFrom(genericType)) {
                throw RuntimeException(
                    "Generic type должен быть аннотацией: ${processor.javaClass.simpleName}",
                )
            }

            @Suppress("UNCHECKED_CAST")
            genericType as Class<out Annotation>
        }

    fun notify(notification: Any, userId: String?) {
        val annotations = getOrderedNotificationAnnotations(notification, targetAnnotation, PRIORITY_FIELD)

        val destinationByAnnotation = notificationDestinationService.getNotificationDestinations(notification, userId)

        for (annotation in annotations) {
            try {
                val annotationClass = annotation.annotationClass.java
                val processor = processorByAnnotation[annotationClass] ?: continue
                val destination = destinationByAnnotation[annotationClass] ?: continue

                @Suppress("UNCHECKED_CAST")
                (processor as NotificationProcessor<Annotation, Any>).process(
                    notification,
                    annotation,
                    destination,
                )

                return
            } catch (e: Throwable) {
                log.error("Произошла ошибка во время отправки уведомления для пользователя $userId", e)
            }
        }

        throw RuntimeException("Уведомления не были отправлены")
    }

    fun batchNotify(notification: Any, userIds: List<UUID>) {
        userIds.chunked(100).forEach { batch -> internalBatchNotify(notification, batch) }
    }

    private fun internalBatchNotify(notification: Any, userIds: List<UUID>) {
        val annotations = getOrderedNotificationAnnotations(notification, targetAnnotation, PRIORITY_FIELD)
        val destinations = notificationDestinationService.getBatchNotificationDestinations(notification, userIds)

        users@ for (userId in userIds) {
            val destinationByAnnotation = destinations[userId]

            if (destinationByAnnotation == null) {
                log.warn("Not found destinations for user $userId")
                continue@users
            }

            annotations@ for (annotation in annotations) {
                try {
                    val annotationClass = annotation.annotationClass.java
                    val processor = processorByAnnotation[annotationClass] ?: continue
                    val destination = destinationByAnnotation[annotationClass] ?: continue

                    @Suppress("UNCHECKED_CAST")
                    (processor as NotificationProcessor<Annotation, Any>).process(
                        notification,
                        annotation,
                        destination,
                    )

                    continue@users
                } catch (e: Throwable) {
                    log.error("Произошла ошибка во время отправки уведомления для пользователя $userId", e)
                }
            }

            log.error("Уведомления для пользователя $userId не были отправлены, сообщение было отправлено на повторную отправку")
            retryNotificationService.retryNotification(notification, userId)
        }
    }
}
