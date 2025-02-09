package ru.melowetty.notificationservice.processor

import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.telegram.TelegramNotification
import ru.melowetty.notificationservice.processor.base.NotificationProcessor

@Component
@Slf4j
class TelegramNotificationProcessor: NotificationProcessor<TelegramNotification> {
    override fun process(notification: Any, data: TelegramNotification) {
        TODO("Not yet implemented")
    }
}