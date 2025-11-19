package ru.melowetty.notificationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.retry.annotation.EnableRetry
import java.util.concurrent.ThreadLocalRandom

@SpringBootApplication
@EnableCaching
@EnableRetry
class NotificationServiceApplication

fun main(args: Array<String>) {
    val instanceId = generateInstanceId()
    System.setProperty("app.instance-id", instanceId)
    runApplication<NotificationServiceApplication>(*args)
}

private fun generateInstanceId(): String {
    val timePart = (System.currentTimeMillis() % 1679616).toString(36)
    val randomPart = (ThreadLocalRandom.current().nextLong(1679616)).toString(36)

    return (timePart.padStart(4, '0') + randomPart.padStart(4, '0'))
        .take(8)
}
