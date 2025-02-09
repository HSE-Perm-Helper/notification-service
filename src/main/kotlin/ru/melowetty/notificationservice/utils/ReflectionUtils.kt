package ru.melowetty.notificationservice.utils

import kotlin.reflect.full.memberProperties

class ReflectionUtils {
    companion object {
        inline fun <reified A> getAnnotationInstance(obj: Any): A? {
            return getAnnotationInstanceFromClass<A>(obj::class.java)
        }

        inline fun <reified A> getAnnotationInstanceFromClass(obj: Class<*>): A? {
            return obj.annotations.find { it is A } as A?
        }

        inline fun <reified T, reified A : Annotation> getPropertyValueByAnnotation(obj: Any): T? {
            val value = obj::class.memberProperties.find {
                it.annotations.any {
                    it is A
                }
            }?.call(obj)
            if (value is T || value is T?) {
                return value as T?
            }
            return null
        }
    }
}