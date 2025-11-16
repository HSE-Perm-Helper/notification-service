package ru.melowetty.notificationservice.exception

class NotRetryableException(
    message: String,
    cause: Throwable? = null
): RuntimeException(message, cause)