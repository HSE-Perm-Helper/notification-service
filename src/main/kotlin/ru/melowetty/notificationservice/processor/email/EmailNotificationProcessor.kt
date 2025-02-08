package ru.melowetty.notificationservice.processor.email

import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.annotation.email.Email
import ru.melowetty.notificationservice.annotation.email.EmailNotification
import ru.melowetty.notificationservice.utils.ReflectionUtils

@Component
@Slf4j
class EmailNotificationProcessor {
    fun process(notification: Any) {
        val emailAnnotation = ReflectionUtils.getAnnotationInstance<EmailNotification>(notification)!!
        val template = emailAnnotation.template

        val email = ReflectionUtils.getPropertyValueByAnnotation<String, Email>(notification) ?: run {
            log.error("Поле с почтой не найдено! Notification: $notification")
            return
        }

        log.info("Шаблон: $template, почта: $email")
    }
}