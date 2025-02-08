package ru.melowetty.notificationservice.processor.email

import kotlin.reflect.full.memberProperties
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.annotation.email.Email
import ru.melowetty.notificationservice.annotation.email.EmailNotification
import ru.melowetty.notificationservice.utils.ReflectionUtils

@Component
@Slf4j
class EmailNotificationProcessor(
    private val templateEngine: TemplateEngine
) {
    fun process(notification: Any) {
        val emailAnnotation = ReflectionUtils.getAnnotationInstance<EmailNotification>(notification)!!
        val template = emailAnnotation.template

        val email = ReflectionUtils.getPropertyValueByAnnotation<String, Email>(notification) ?: run {
            log.error("Поле с почтой не найдено! Notification: $notification")
            return
        }

        val message = renderMessage(notification, template)
    }

    fun renderMessage(instance: Any, template: String): String {
        val ctx = Context()

        instance::class.memberProperties.forEach {
            ctx.setVariable(it.name, it.call(instance))
        }

        return templateEngine.process(template, ctx)
    }
}