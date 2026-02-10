package com.example.sakina.utils

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatClockTime(context: Context, timeMillis: Long): String {
    val pattern = if (DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timeMillis))
}

