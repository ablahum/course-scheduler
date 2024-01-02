package com.dicoding.courseschedule.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.ui.home.HomeActivity
import com.dicoding.courseschedule.util.NOTIFICATION_CHANNEL_ID
import com.dicoding.courseschedule.util.NOTIFICATION_CHANNEL_NAME
import com.dicoding.courseschedule.util.NOTIFICATION_ID
import com.dicoding.courseschedule.util.executeThread
import java.util.Calendar

class DailyReminder : BroadcastReceiver() {

    companion object {
        private const val TAG = "DailyReminder"

        fun setDailyReminder(context: Context, isEnabled: Boolean) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, DailyReminder::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_MUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 6)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            Log.d(TAG, "Scheduling daily reminder at: ${calendar.time}")

            if (isEnabled) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
                Log.d(TAG, "Daily reminder scheduled.")
            } else {
                alarmManager.cancel(pendingIntent)
                Log.d(TAG, "Daily reminder canceled.")
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Daily reminder received.")
        executeThread {
            val repository = DataRepository.getInstance(context)
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val courses = repository.getTodaySchedule(currentDay)

            courses.let {
                if (it.isNotEmpty()) {
                    Log.d(TAG, "Today's schedule is not empty. Showing notification.")
                    showNotification(context, it)
                } else {
                    Log.d(TAG, "Today's schedule is empty. No notification.")
                }
            }
        }
    }

    private fun showNotification(context: Context, content: List<Course>) {
        Log.d(TAG, "Building and showing notification.")
        val notificationStyle = NotificationCompat.InboxStyle()

        content.forEach {
            val timeString = context.resources.getString(R.string.notification_message_format)
            val courseData = String.format(timeString, it.startTime, it.endTime, it.courseName)
            notificationStyle.addLine(courseData)
        }

        createNotificationChannel(context)

        val intent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.pref_notify_name))
            .setContentText(context.getString(R.string.pref_notify_summary))
            .setSmallIcon(R.drawable.ic_notifications)
            .setStyle(notificationStyle)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

        Toast.makeText(context, "Notification shown with ID: $NOTIFICATION_ID", Toast.LENGTH_SHORT)
            .show()

        Log.d(TAG, "Notification shown with ID: $NOTIFICATION_ID")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
