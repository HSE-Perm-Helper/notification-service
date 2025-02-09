package ru.melowetty.notificationservice.processor

import org.springframework.stereotype.Component
import ru.melowetty.notificationservice.annotation.Slf4j
import ru.melowetty.notificationservice.annotation.telegram.TelegramNotification
import ru.melowetty.notificationservice.processor.base.NotificationProcessor

@Component
@Slf4j
class TelegramNotificationProcessor: NotificationProcessor<TelegramNotification, Long> {
    override fun process(notification: Any, data: TelegramNotification, destination: Long) {
        TODO("Not yet implemented")
    }
}