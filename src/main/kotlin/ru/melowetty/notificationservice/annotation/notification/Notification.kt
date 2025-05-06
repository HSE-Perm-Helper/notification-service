package ru.melowetty.notificationservice.annotation.notification

import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Notification(
    val destinationAnnotation: KClass<out Annotation>,
)
