package ru.melowetty.notificationservice.utils

import java.util.*

object RequestIdGenerator {
    fun generate(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}