package ru.melowetty.notificationservice.utils

import java.time.format.DateTimeFormatter

object Constants {
    const val DATE_PATTERN = "dd.MM.yyyy"

    val DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_PATTERN)
}