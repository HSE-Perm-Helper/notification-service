package ru.melowetty.notificationservice.model.notification.timetable

import ru.melowetty.notificationservice.annotation.notification.ProcessableNotification
import ru.melowetty.notificationservice.annotation.notification.telegram.TelegramNotification
import ru.melowetty.notificationservice.model.notification.base.NotificationType
import ru.melowetty.notificationservice.model.notification.telegram.TelegramKeyboardNotification
import ru.melowetty.notificationservice.model.timetable.TimetableInfo
import ru.melowetty.notificationservice.utils.CommonUtils
import ru.melowetty.notificationservice.utils.TimetableUtils
import java.time.DayOfWeek

@TelegramNotification("telegram/changed_timetable.txt")
@ProcessableNotification(NotificationType.TIMETABLE_CHANGED)
data class ChangedTimetableNotification(
    val timetableInfo: TimetableInfo,
    val changedDays: List<DayOfWeek>
) : TelegramKeyboardNotification {
    val text by lazy {
        renderText()
    }

    private fun renderText(): String {
        val timetableTypeDisplayName = TimetableUtils.getNameByTimetableType(timetableInfo)
        val formattedDays = CommonUtils.formatOutputArray(changedDays.map { TimetableUtils.getPluralizedDay(it.name) })
        return "В расписании на *$timetableTypeDisplayName* появились изменения на *$formattedDays*"
    }

    override fun getCallbackQueryKeyboard(): List<List<Pair<String, String>>> {
        return TimetableUtils.getTimetablesButtons(listOf(timetableInfo)).map { listOf(it) }
    }
}