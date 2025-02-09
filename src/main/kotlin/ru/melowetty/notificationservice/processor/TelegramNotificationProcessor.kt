package ru.melowetty.notificationservice.processor

import kotlin.reflect.full.memberProperties
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Component
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.annotation.telegram.TelegramNotification
import ru.melowetty.notificationservice.processor.base.NotificationProcessor

@Component
@Slf4j
class TelegramNotificationProcessor(
    private val env: Environment,
    private val templateEngine: TemplateEngine
): NotificationProcessor<TelegramNotification, Long> {
    private val botToken: String = env["spring.telegram.token"]!!

    private val telegramBot = OkHttpTelegramClient(botToken)

    override fun process(notification: Any, data: TelegramNotification, destination: Long) {
        val text = renderMessage(notification, data.template)

        val sendMessage = SendMessage.builder()
            .text(text)
            .chatId(destination)
            .parseMode(ParseMode.MARKDOWN)
            .build()

        telegramBot.execute(sendMessage)

        log.info("Сообщение ${notification::class.java.simpleName} пользователю $destination успешно отправлено")
    }

    fun renderMessage(instance: Any, template: String): String {
        val ctx = Context()

        instance::class.memberProperties.forEach {
            ctx.setVariable(it.name, it.call(instance))
        }

        return templateEngine.process(template, ctx)
    }
}