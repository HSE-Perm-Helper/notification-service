package ru.melowetty.notificationservice.annotation.email

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EmailNotification(
    val template: String
)
