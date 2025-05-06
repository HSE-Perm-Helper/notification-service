package ru.melowetty.notificationservice.utils

import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

class ReflectionUtils {
    companion object {
        inline fun <reified A> getAnnotationInstance(obj: Any): A? = getAnnotationInstanceFromClass<A>(obj::class.java)

        inline fun <reified A> getAnnotationInstanceFromClass(obj: Class<*>): A? = obj.annotations.find { it is A } as A?

        inline fun <reified T, reified A : Annotation> getPropertyValueByAnnotation(obj: Any): T? {
            val value = obj::class.memberProperties.find { it.annotations.any { it is A } }?.call(obj)
            if (value is T || value is T?) {
                return value as T?
            }
            return null
        }

        fun getAnnotationFieldValue(
            annotatedElement: KClass<*>,
            annotationClass: Annotation,
            fieldName: String
        ): Any? {
            val annotation = annotatedElement.findAnnotations(annotationClass::class).firstOrNull()
                ?: return null

            return annotation::class.memberProperties
                .firstOrNull { it.name == fieldName }
                ?.call(annotationClass)
        }

    }
}
