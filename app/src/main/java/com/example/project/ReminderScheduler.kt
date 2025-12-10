package com.example.project

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object ReminderScheduler {

    fun scheduleReminder(context: Context, item: TodoItem) {
        val remindAt = item.remindAt ?: return

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", item.title)
            putExtra("id", item.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            remindAt,
            pendingIntent
        )
    }
}
