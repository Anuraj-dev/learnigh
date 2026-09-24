package com.anuraj.learnigh.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dayKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    fun formatDate(millis: Long?): String =
        if (millis == null) "—" else displayFormat.format(Date(millis))

    fun todayKey(): String = dayKeyFormat.format(Date())

    fun daysUntil(deadline: Long?): Int? {
        if (deadline == null) return null
        val now = System.currentTimeMillis()
        val diff = deadline - startOfDay(now)
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }

    fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun monthYear(millis: Long): String = monthYearFormat.format(Date(millis))

    fun dueLabel(deadline: Long?): String {
        val days = daysUntil(deadline) ?: return "No deadline"
        return when {
            days < 0 -> "${-days}d overdue"
            days == 0 -> "Due today"
            days == 1 -> "Due tomorrow"
            days <= 7 -> "Due in ${days}d"
            else -> formatDate(deadline)
        }
    }
}
