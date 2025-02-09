package ru.melowetty.notificationservice.processor

import org.springframework.core.ResolvableType
import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.Notification
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.processor.base.NotificationProcessor
import ru.melowetty.notificationservice.utils.ReflectionUtils

@Component
@Slf4j
class CommonNotificationProcessor(
    private val processors: List<NotificationProcessor<*>>
) {
    private val processorByAnnotation = getProcessorByAnnotationMap()
    private val targetAnnotation = Notification::class.java

    private final fun getProcessorByAnnotationMap(): Map<Class<out Annotation>, NotificationProcessor<*>> {
        return processors.associateBy { processor ->
            val type = ResolvableType.forClass(processor.javaClass).`as`(NotificationProcessor::class.java)

            val genericType = type.getGeneric(0).resolve()
                ?: throw RuntimeException("Не все процессоры верно настроены, запуск невозможен: ${processor.javaClass.simpleName}")

            if (!Annotation::class.java.isAssignableFrom(genericType)) {
                throw RuntimeException("Generic type должен быть аннотацией: ${processor.javaClass.simpleName}")
            }

            @Suppress("UNCHECKED_CAST")
            genericType as Class<out Annotation>
        }
    }

    fun notify(notification: Any) {
        val annotations = notification::class.java.annotations.filter {
            it.annotationClass.java.isAnnotationPresent(targetAnnotation)
        }.sortedBy {
            val notificationInstance = ReflectionUtils.getAnnotationInstanceFromClass<Notification>(it.annotationClass.java)
            notificationInstance!!.order
        }

        var processed = false

        for (annotation in annotations) {
            try {
                val processor = processorByAnnotation[annotation.annotationClass.java]
                    ?: continue

                val annotationInstance = annotation
                    ?: annotation.annotationClass.java.getDeclaredConstructor().newInstance()

                @Suppress("UNCHECKED_CAST")
                (processor as NotificationProcessor<Annotation>).process(notification, annotationInstance)

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