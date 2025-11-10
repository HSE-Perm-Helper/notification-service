package ru.melowetty.notificationservice.utils

import ru.melowetty.notificationservice.model.timetable.TimetableInfo
import ru.melowetty.notificationservice.model.timetable.TimetableType

object TimetableUtils {
    private const val CALLBACK_TAG = "timetable"
    private const val CALLBACK_SEPARATOR = ":"

    private val days = mapOf(
            "MONDAY" to "понедельник",
            "TUESDAY" to "вторник",
            "WEDNESDAY" to "среду",
            "THURSDAY" to "четверг",
            "FRIDAY" to "пятницу",
            "SATURDAY" to "субботу",
            "SUNDAY" to "воскресенье"
    )

    fun getTimetablesButtons(timetableInfos: List<TimetableInfo>): List<Pair<String, String>> {
        return timetableInfos.map { getButtonTextByScheduleInfo(it) to getButtonCallbackDataByScheduleInfo(it) }
    }

    fun getNameByTimetableType(info: TimetableInfo): String {
        return when (info.type) {
            TimetableType.WEEK_SCHEDULE -> "${info.number} неделю"
            TimetableType.SESSION_SCHEDULE -> "сессию"
            TimetableType.QUARTER_SCHEDULE -> "модуль"
        }
    }

    fun getPluralizedDay(day: String): String {
        return days[day] ?: day
    }

    private fun getButtonTextByScheduleInfo(timetableInfo: TimetableInfo): String {
        return when (timetableInfo.type) {
            TimetableType.WEEK_SCHEDULE -> {
                val number = timetableInfo.number ?: return "N/a"
                "Неделя $number, ${timetableInfo.start.format(Constants.DATE_FORMAT)} — ${timetableInfo.end.format(Constants.DATE_FORMAT)}"
            }
            TimetableType.SESSION_SCHEDULE -> {
                "Сессия, ${timetableInfo.start.format(Constants.DATE_FORMAT)} — ${timetableInfo.end.format(Constants.DATE_FORMAT)}"
            }
            TimetableType.QUARTER_SCHEDULE -> {
                val number = timetableInfo.number ?: return "N/a"
                "Базовое расписание на $number модуль"
            }
        }
    }

    private fun getButtonCallbackDataByScheduleInfo(timetableInfo: TimetableInfo): String {
        return "$CALLBACK_TAG$CALLBACK_SEPARATOR${timetableInfo.id}"
    }
}