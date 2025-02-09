package ru.melowetty.notificationservice.processor

import jakarta.mail.internet.MimeMessage
import java.nio.charset.StandardCharsets
import kotlin.reflect.full.memberProperties
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.annotation.email.EmailNotification
import ru.melowetty.notificationservice.processor.base.NotificationProcessor

@Component
@Slf4j
class EmailNotificationProcessor(
    private val templateEngine: TemplateEngine,
    private val emailSender: JavaMailSender
): NotificationProcessor<EmailNotification, String> {
    @Value("\${spring.mail.username}")
    private lateinit var emailFrom: String

    @Value("\${spring.mail.display-name}")
    private lateinit var displayName: String

    override fun process(notification: Any, data: EmailNotification, destination: String) {
        val template = data.template

        val message = renderMessage(notification, template)
        val title = extractTitleFromHtml(message)

        val mimeMessage = buildMimeMessage(destination, message, title)

        emailSender.send(mimeMessage)

        log.info("Письмо ${notification::class.java.simpleName} на почту $destination успешно отправлено")
    }

    fun extractTitleFromHtml(html: String): String {
        val jsoup = Jsoup.parse(html)
        return jsoup.title()
    }

    fun buildMimeMessage(email: String, text: String, subject: String): MimeMessage {
        val mimeMessage: MimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(
            mimeMessage,
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name()
        )

        helper.setTo(email)
        helper.setText(text, true)
        helper.setSubject(subject)
        helper.setFrom(emailFrom, displayName)

        return mimeMessage
    }

    fun renderMessage(instance: Any, template: String): String {
        val ctx = Context()

        instance::class.memberProperties.forEach {
            ctx.setVariable(it.name, it.call(instance))
        }

        return templateEngine.process(template, ctx)
    }
}