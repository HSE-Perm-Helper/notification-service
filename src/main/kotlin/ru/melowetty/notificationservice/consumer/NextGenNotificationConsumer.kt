package ru.melowetty.notificationservice.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.KafkaNotification
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log

@Component
@Slf4j
class NextGenNotificationConsumer(
    private val objectMapper: ObjectMapper
) {
    private val mapperByNotificationType: Map<String, Class<*>> = getNotificationsClassesMapper()

    companion object {
        private const val NOTIFICATION_TYPE_FIELD = "notificationType"
    }

    private fun getNotificationsClassesMapper(): Map<String, Class<*>> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(KafkaNotification::class.java))

        val classes = scanner.findCandidateComponents("ru.melowetty.notificationservice")
            .asSequence()
            .map {
                Class.forName(it.beanClassName)
            }
            .associateBy {
                val annotation = it.annotations.find {
                    it is KafkaNotification
                } as KafkaNotification

                annotation.notificationType.type
            }

        return classes
    }

    @KafkaListener(
        topics = ["\${spring.kafka.topic.next-gen-notifications}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactoryHashMap"
    )
    fun consumeNewNotification(notification: HashMap<String, Any?>) {
        val notificationType = notification[NOTIFICATION_TYPE_FIELD]
        val targetType = mapperByNotificationType[notificationType]
            ?: run {
                log.error("Уведомление с таким типом не найдено!")
                return
            }

        val valueAsStr = objectMapper.writeValueAsString(notification)
        val valueAsObject = objectMapper.readValue(valueAsStr, targetType)
    }
}