package ru.melowetty.notificationservice.processor

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.melowetty.notificationservice.annotation.Priority

class CommonNotificationProcessorTest {
    companion object {
        private const val PRIORITY_FIELD = "priority"
        private val targetAnnotation = BaseNotification::class.java
    }

    @Test
    fun `test ordered notifications when firstly email`() {
        val notification = FirstlyEmailNotification()

        val annotations = CommonNotificationProcessor.getOrderedNotificationAnnotations(notification, targetAnnotation, PRIORITY_FIELD)
            .map { it.annotationClass }
            .toList()

        Assertions.assertEquals(2, annotations.size)
        Assertions.assertEquals(Email::class, annotations.first())
        Assertions.assertEquals(Telegram::class, annotations[1])
    }

    @Test
    fun `test ordered notifications when firstly telegram`() {
        val notification = FirstlyTelegramNotification()

        val annotations = CommonNotificationProcessor.getOrderedNotificationAnnotations(notification, targetAnnotation, PRIORITY_FIELD)
            .map { it.annotationClass }
            .toList()

        Assertions.assertEquals(2, annotations.size)
        Assertions.assertEquals(Telegram::class, annotations.first())
        Assertions.assertEquals(Email::class, annotations[1])
    }

    @Test
    fun `test ordered notifications without priority`() {
        val notification = WithoutPriorityNotification()

        val annotations = CommonNotificationProcessor.getOrderedNotificationAnnotations(notification, targetAnnotation, PRIORITY_FIELD)
            .map { it.annotationClass }
            .toList()

        Assertions.assertEquals(1, annotations.size)
        Assertions.assertEquals(WithoutPriority::class, annotations.first())
    }

    annotation class BaseNotification

    @BaseNotification
    annotation class Email(
        val priority: Priority = Priority(1)
    )

    @BaseNotification
    annotation class Telegram(
        val priority: Priority = Priority(1)
    )

    @BaseNotification
    annotation class WithoutPriority

    @Email(priority = Priority(1))
    @Telegram(priority = Priority(2))
    inner class FirstlyEmailNotification

    @Email(priority = Priority(2))
    @Telegram(priority = Priority(1))
    inner class FirstlyTelegramNotification

    @WithoutPriority
    inner class WithoutPriorityNotification
}