package com.anuraj.learnigh.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateUtils {
    private val zone: ZoneId = ZoneId.systemDefault()
    private val displayFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    private val monthYearFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    fun formatDate(millis: Long?): String =
        millis?.let { Instant.ofEpochMilli(it).atZone(zone).format(displayFormat) } ?: "—"

    fun todayKey(): String = LocalDate.now(zone).toString()

    fun daysUntil(deadline: Long?): Int? {
        if (deadline == null) return null
        val target = Instant.ofEpochMilli(deadline).atZone(zone).toLocalDate()
        return ChronoUnit.DAYS.between(LocalDate.now(zone), target).toInt()
    }

    fun startOfDay(millis: Long): Long {
        val date = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
        return date.atStartOfDay(zone).toInstant().toEpochMilli()
    }

    fun monthYear(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(zone).format(monthYearFormat)

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

    fun toPickerDateMillis(localMillis: Long?): Long? = localMillis?.let { millis ->
        Instant.ofEpochMilli(millis)
            .atZone(zone)
            .toLocalDate()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    fun fromPickerDateMillis(pickerMillis: Long?): Long? = pickerMillis?.let { millis ->
        Instant.ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
    }
}
