package com.example.project

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

fun setReminder(context: Context, timeMillis: Long, message: String) {
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("text", message)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        timeMillis.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        timeMillis,
        pendingIntent
    )
}
