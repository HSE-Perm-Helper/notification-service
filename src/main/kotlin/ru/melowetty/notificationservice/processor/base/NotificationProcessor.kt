package ru.melowetty.notificationservice.processor.base

interface NotificationProcessor<A: Annotation, D: Any> {
    fun process(notification: Any, data: A, destination: D)
}