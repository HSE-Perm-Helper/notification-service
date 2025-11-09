package ru.melowetty.notificationservice.utils

object CommonUtils {
    fun formatOutputArray(items: List<*>): String {
        return when (items.size) {
            0 -> ""
            1 -> items[0].toString()
            2 -> "${items[0]} и ${items[1]}"
            else -> {
                val last = items.last()
                val rest = items.dropLast(1).joinToString(", ")
                "$rest и $last"
            }
        }
    }
}