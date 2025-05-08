package ru.melowetty.notificationservice.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Priority(
    val value: Int
)
