package com.phrase.android.wx.utils

import android.content.Context
import android.icu.text.RelativeDateTimeFormatter
import android.text.format.DateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class DateTimeUtils {

    companion object {

        fun getTimeFormat(context: Context, date: Date, isDefault: Boolean = true)
                : String {
            if (isDefault) {
                return DateFormat.getTimeFormat(context).format(date)
                // Output: 9:30 PM (en-US) | 21:30 (en-GB) | 9:30 p.m. (es-US) | 21:32 (es)
            } else {
                // If 'Use locale time' option is true, the result will be same.
                val skeletonTime = if (DateFormat.is24HourFormat(context)) {
                    "Hm"
                } else {
                    "hm"
                }
                val patternTime =
                    DateFormat.getBestDateTimePattern(Locale.getDefault(), skeletonTime)

                return DateFormat.format(patternTime, date).toString()
                // Output: 9:30 PM (en-US) | 21:30 (en-GB) | 9:30 p.m. (es-US) | 21:32 (es)
            }
        }

        fun getDateFormat(context: Context, date: Date, isDefault: Boolean = true)
                : String {
            if (isDefault) {
                return DateFormat.getMediumDateFormat(context).format(date)
                // Output: Sep, 7, 2020 (en-US) | 7 Sep 2020 (en-GB) | 7 sep. 2020 (es-US) | 7 sept. 2020 (es)
            } else {
                val skeletonDate = "EEEddMMM"
                val patternDate =
                    DateFormat.getBestDateTimePattern(Locale.getDefault(), skeletonDate)

                return DateFormat.format(patternDate, date).toString()
                // Output: Mon, Sep 07 (en-US) | Mon, 07 Sep (en-GB) | lun., 07 de sep. (es-US) | lun., 07 sept. (es)
            }
        }

        fun getRelativeDateFormat(context: Context, date: Date): String {
            val current = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val days = ChronoUnit.DAYS.between(current, LocalDate.now())

            val fmt = RelativeDateTimeFormatter.getInstance(Locale.getDefault())
            return when (days) {
                0L -> fmt.format(
                    RelativeDateTimeFormatter.Direction.THIS,
                    RelativeDateTimeFormatter.AbsoluteUnit.DAY
                )
                // Output: today (en-US) | today (en-GB) | hoy (es-US) | hoy (es)
                1L -> fmt.format(
                    RelativeDateTimeFormatter.Direction.LAST,
                    RelativeDateTimeFormatter.AbsoluteUnit.DAY
                )
                // Output: yesterday (en-US) | yesterday (en-GB) | ayer (es-US) | ayer (es)
                else -> getDateFormat(context, date, false)
                // Output: Mon, Sep 07 (en-US) | Mon, 07 Sep (en-GB) | lun., 07 de sep. (es-US) | lun., 07 sept. (es)
            }
        }
    }
}
