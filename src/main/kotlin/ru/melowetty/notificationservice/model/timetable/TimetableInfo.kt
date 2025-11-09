package ru.melowetty.notificationservice.model.timetable

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import ru.melowetty.notificationservice.utils.Constants
import java.time.LocalDate

data class TimetableInfo(
    val id: String,

    val number: Int?,

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    val start: LocalDate,

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    val end: LocalDate,

    @JsonProperty("scheduleType")
    val type: TimetableType,
)
