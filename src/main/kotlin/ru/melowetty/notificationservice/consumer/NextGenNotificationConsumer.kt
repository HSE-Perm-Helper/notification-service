package ru.melowetty.notificationservice.consumer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.notification.ProcessableNotification
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.exception.NotRetryableException
import ru.melowetty.notificationservice.processor.CommonNotificationProcessor
import ru.melowetty.notificationservice.utils.ReflectionUtils
import java.util.UUID

@Component
@Slf4j
class NextGenNotificationConsumer(
    private val objectMapper: ObjectMapper,
    private val notificationProcessor: CommonNotificationProcessor,
) {
    private val mapperByNotificationType: Map<String, Class<*>> = getNotificationsClassesMapper()

    companion object {
        private const val NOTIFICATION_TYPE_FIELD = "notificationType"
        private const val USER_ID_FIELD = "userId"
    }

    private fun getNotificationsClassesMapper(): Map<String, Class<*>> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(ProcessableNotification::class.java))

        val classes =
            scanner
                .findCandidateComponents("ru.melowetty.notificationservice")
                .asSequence()
                .map { Class.forName(it.beanClassName) }
                .associateBy {
                    val annotation = ReflectionUtils.getAnnotationInstanceFromClass<ProcessableNotification>(it)!!

                    annotation.notificationType.type
                }

        return classes
    }

    @KafkaListener(
        topics = ["\${spring.kafka.topic.next-gen-notifications}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactoryHashMap",
    )
    @RetryableTopic(
        attempts = "6",
        autoCreateTopics = "true",
        backoff = Backoff(1000, multiplier = 5.0, maxDelay = 3_125_000),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        exclude = [JsonMappingException::class, JsonProcessingException::class, NotRetryableException::class],
    )
    fun consumeNewNotification(notification: HashMap<String, Any?>) {
        val notificationType = notification[NOTIFICATION_TYPE_FIELD]
        val targetType =
            mapperByNotificationType[notificationType]
                ?: run {
                    log.error("Уведомление с таким типом не найдено, notification: $$notification")
                    throw NotRetryableException(
                        "Уведомление с таким типом не найдено, notification: $$notification",
                    )
                }

        val valueAsStr = objectMapper.writeValueAsString(notification)

        try {
            val valueAsObject = objectMapper.readValue(valueAsStr, targetType)

            val userId = notification[USER_ID_FIELD]
            when (userId) {
                is String -> {
                    notificationProcessor.notify(valueAsObject, userId)
                }
                is List<*> -> {
                    val userIds = (userId as? List<String>)
                        ?.map { UUID.fromString(it) }
                        ?: throw NotRetryableException("Неверный формат userId, ожидается List<String>, а дано: $userId")
                    notificationProcessor.batchNotify(valueAsObject, userIds)
                }
                else -> {
                    notificationProcessor.notify(valueAsObject, null)
                }
            }
        } catch (e: JsonMappingException) {
            log.error("Ошибка во время маппинга нотификации: $notification")
            throw e
        } catch (e: JsonProcessingException) {
            log.error("Ошибка во время процессинга нотификации: $notification")
            throw e
        }
    }
}
