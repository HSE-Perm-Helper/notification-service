package ru.melowetty.notificationservice.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Notification(
    val order: Int
)
