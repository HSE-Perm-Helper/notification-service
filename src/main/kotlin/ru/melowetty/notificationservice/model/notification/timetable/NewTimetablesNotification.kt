package ru.melowetty.notificationservice.model.notification.timetable

import ru.melowetty.notificationservice.annotation.notification.ProcessableNotification
import ru.melowetty.notificationservice.annotation.notification.telegram.TelegramNotification
import ru.melowetty.notificationservice.model.notification.base.NotificationType
import ru.melowetty.notificationservice.model.notification.telegram.TelegramKeyboardNotification
import ru.melowetty.notificationservice.model.timetable.TimetableInfo
import ru.melowetty.notificationservice.model.timetable.TimetableType
import ru.melowetty.notificationservice.utils.CommonUtils
import ru.melowetty.notificationservice.utils.TimetableUtils

@TelegramNotification("telegram/new_timetables.txt")
@ProcessableNotification(NotificationType.TIMETABLE_ADDED)
data class NewTimetablesNotification(
    val timetables: List<TimetableInfo>
) : TelegramKeyboardNotification {
    val text: String by lazy { render() }

    override fun getCallbackQueryKeyboard(): List<List<Pair<String, String>>> {
        return TimetableUtils.getTimetablesButtons(timetables).map {
            listOf(it)
        }
    }

    fun render(): String {
        val grouped = timetables.groupBy { it.type }

        val parts = mutableListOf<String>()

        if (grouped.containsKey(TimetableType.QUARTER_SCHEDULE)) {
            parts.add("базовое расписание")
        }
        if (grouped.containsKey(TimetableType.WEEK_SCHEDULE)) {
            val weeks = grouped[TimetableType.WEEK_SCHEDULE]!!
                .mapNotNull { it.number }
            val mergedWeeks = CommonUtils.formatOutputArray(weeks)
            parts.add("расписание на $mergedWeeks неделю")
        }
        if (grouped.containsKey(TimetableType.SESSION_SCHEDULE)) {
            parts.add("расписание на сессию")
        }

        return "Добавлено ${CommonUtils.formatOutputArray(parts)}!"
    }

}
