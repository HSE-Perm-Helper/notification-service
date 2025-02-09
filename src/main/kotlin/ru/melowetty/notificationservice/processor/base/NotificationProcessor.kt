package ru.melowetty.notificationservice.processor.base

interface NotificationProcessor<A: Annotation> {
    fun process(notification: Any, data: A)
}