package ru.melowetty.notificationservice.exception

class NotRetryableException(
    message: String
): RuntimeException(message = message)