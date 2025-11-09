package ru.melowetty.notificationservice.model.notification.telegram

interface TelegramKeyboardNotification {
    fun getCallbackQueryKeyboard(): List<List<Pair<String, String>>>
}