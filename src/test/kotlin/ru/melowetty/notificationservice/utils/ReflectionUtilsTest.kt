package ru.melowetty.notificationservice.utils

import kotlin.reflect.full.findAnnotations
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ReflectionUtilsTest {
    @Test
    fun `test get annotation field value when field is exists`() {
        val cls = TestClass::class
        val value = ReflectionUtils.getAnnotationFieldValue(cls, cls.findAnnotations(TestAnnotation::class).first(), "value")

        Assertions.assertEquals(1, value)
    }

    @Test
    fun `test get annotation field value when field is not exists`() {
        val cls = TestClass::class
        val value = ReflectionUtils.getAnnotationFieldValue(cls, cls.findAnnotations(TestAnnotation::class).first(), "value1")

        Assertions.assertNull(value)
    }

    @TestAnnotation(1)
    inner class TestClass

    annotation class TestAnnotation(
        val value: Int
    )
}