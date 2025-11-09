package ru.melowetty.notificationservice.processor

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Component
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.Slf4j.Companion.log
import ru.melowetty.notificationservice.annotation.notification.telegram.TelegramNotification
import ru.melowetty.notificationservice.model.notification.telegram.TelegramKeyboardNotification
import ru.melowetty.notificationservice.processor.base.NotificationProcessor
import kotlin.reflect.full.memberProperties

@Component
@Slf4j
class TelegramNotificationProcessor(
    private val env: Environment,
    private val templateEngine: TemplateEngine,
) : NotificationProcessor<TelegramNotification, Long> {
    private val botToken: String = env["spring.telegram.token"]!!

    private val telegramBot = OkHttpTelegramClient(botToken)

    @Value("\${spring.telegram.notification-prefix}")
    private lateinit var notificationPrefix: String

    override fun process(
        notification: Any,
        data: TelegramNotification,
        destination: Long,
    ) {
        val text = notificationPrefix + " " + renderMessage(notification, data.template)
        val keyboard = renderKeyboard(notification)

        val sendMessage =
            SendMessage
                .builder()
                .text(text)
                .chatId(destination)
                .parseMode(ParseMode.MARKDOWN)
                .apply {
                    if (keyboard != null) replyMarkup(keyboard)
                }
                .build()

        telegramBot.execute(sendMessage)

        log.info(
            "Сообщение ${notification::class.java.simpleName} пользователю $destination успешно отправлено",
        )
    }

    fun renderMessage(
        instance: Any,
        template: String,
    ): String {
        val ctx = Context()

        instance::class.memberProperties.forEach { ctx.setVariable(it.name, it.call(instance)) }

        return templateEngine.process(template, ctx)
    }

    fun renderKeyboard(instance: Any): InlineKeyboardMarkup? {
        return if (instance is TelegramKeyboardNotification) {
            val rows = instance.getCallbackQueryKeyboard().map {
                InlineKeyboardRow(
                    it.map { button ->
                        InlineKeyboardButton.builder()
                            .text(button.first)
                            .callbackData(button.second)
                            .build()
                    }
                )
            }

            InlineKeyboardMarkup(rows)
        } else null
    }
}
