package ru.melowetty.notificationservice.utils

import kotlin.reflect.full.findAnnotations
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.melowetty.notificationservice.utils.ReflectionUtils.Companion.extractInnerAnnotations

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

    @Test
    fun `test get field values by annotations`() {
        val obj = object {
            @TestFieldAnnotation
            val field1 = "field1"

            @TestFieldSecondAnnotation
            val field2 = 123
        }

        val annotations = listOf(TestFieldAnnotation::class, TestFieldSecondAnnotation::class)
        val result = ReflectionUtils.getFieldValuesByAnnotations(obj, annotations)

        Assertions.assertEquals(2, result.size)
        Assertions.assertEquals("field1", result[TestFieldAnnotation::class])
        Assertions.assertEquals(123, result[TestFieldSecondAnnotation::class])
    }

    @Test
    fun `test get inner annotation of class`() {
        @TestAnnotation(1)
        class ObjWithInnerAnnotation

        val annotations = ObjWithInnerAnnotation::class.extractInnerAnnotations<InnerTestAnnotation>()

        Assertions.assertEquals(1, annotations.size)
        Assertions.assertEquals(TestAnnotation::class, annotations[0].first.annotationClass)
    }

    @TestAnnotation(1)
    inner class TestClass

    @Retention(AnnotationRetention.RUNTIME)
    annotation class InnerTestAnnotation

    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestAnnotation2

    @InnerTestAnnotation
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestAnnotation(
        val value: Int
    )

    annotation class TestFieldAnnotation
    annotation class TestFieldSecondAnnotation
}