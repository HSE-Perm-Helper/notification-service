package ru.melowetty.notificationservice.processor

import org.springframework.core.ResolvableType
import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.Priority
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.annotation.notification.Notification
import ru.melowetty.notificationservice.processor.base.NotificationProcessor
import ru.melowetty.notificationservice.utils.ReflectionUtils

@Component
@Slf4j
final class CommonNotificationProcessor(
    private val processors: List<NotificationProcessor<*, *>>,
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

    fun notify(notification: Any) {
        val annotations = getOrderedNotificationAnnotations(notification, targetAnnotation, PRIORITY_FIELD)

        var processed = false

        for (annotation in annotations) {
            try {
                val processor = processorByAnnotation[annotation.annotationClass.java] ?: continue

//                val destination =
//                    ReflectionUtils.getPropertyValueByAnnotation<Any, NotificationDestination>(notification)
//                        ?: run {
//                            log.error("Поле с получателем не найдено! Notification: $notification")
//                            return
//                        } // заменить

//                @Suppress("UNCHECKED_CAST")
//                (processor as NotificationProcessor<Annotation, Any>).process(
//                    notification,
//                    annotation,
//                    destination,
//                )

                processed = true
                break
            } catch (e: RuntimeException) {
                log.error("Произошла ошибка во время отправки уведомления", e)
            }
        }

        if (!processed) {
            throw RuntimeException("Уведомления не были отправлены")
        }
    }
}
