package ru.melowetty.notificationservice.model.notification

enum class NotificationType(
    val type: String
) {
    EMAIL_VERIFICATION("EMAIL_VERIFICATION"),
    EMAIL_IS_VERIFIED("EMAIL_IS_VERIFIED")
}