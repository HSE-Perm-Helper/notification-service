package ru.melowetty.notificationservice.consumer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.utils.ExponentialBackoff
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.KafkaNotification
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.processor.CommonNotificationProcessor
import ru.melowetty.notificationservice.utils.ReflectionUtils

@Component
@Slf4j
class NextGenNotificationConsumer(
    private val objectMapper: ObjectMapper,
    private val notificationProcessor: CommonNotificationProcessor
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
                val annotation = ReflectionUtils.getAnnotationInstanceFromClass<KafkaNotification>(it)!!

                annotation.notificationType.type
            }

        return classes
    }

    @KafkaListener(
        topics = ["\${spring.kafka.topic.next-gen-notifications}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactoryHashMap"
    )
    @RetryableTopic(
        attempts = "5",
        autoCreateTopics = "true",
        backoff = Backoff(1000, multiplier = 3.0, maxDelay = 81000),
        dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    fun consumeNewNotification(notification: HashMap<String, Any?>) {
        val notificationType = notification[NOTIFICATION_TYPE_FIELD]
        val targetType = mapperByNotificationType[notificationType]
            ?: run {
                log.error("Уведомление с таким типом не найдено, notification: $$notification")
                return
            }

        val valueAsStr = objectMapper.writeValueAsString(notification)

        try {
            val valueAsObject = objectMapper.readValue(valueAsStr, targetType)
            notificationProcessor.notify(valueAsObject)
        } catch (e: JsonMappingException) {
            log.error("Ошибка во время маппинга нотификации: $notification")
        } catch (e: JsonProcessingException) {
            log.error("Ошибка во время процессинга нотификации: $notification")
        }
    }
}