package ru.melowetty.notificationservice.model.notification.base

enum class NotificationType(
    val type: String,
) {
    EMAIL_VERIFICATION("EMAIL_VERIFICATION"),
    EMAIL_IS_VERIFIED("EMAIL_IS_VERIFIED"),
    SERVICE_WARNING("SERVICE_WARNING"),
    TIMETABLE_ADDED("TIMETABLE_ADDED"),
    TIMETABLE_CHANGED("TIMETABLE_CHANGED"),
}
