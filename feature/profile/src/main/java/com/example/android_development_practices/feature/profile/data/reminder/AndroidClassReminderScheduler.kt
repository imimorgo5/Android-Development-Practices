package com.example.android_development_practices.feature.profile.data.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.android_development_practices.feature.profile.domain.ClassReminder
import com.example.android_development_practices.feature.profile.domain.ClassReminderScheduler
import com.example.android_development_practices.feature.profile.domain.ReminderAccess
import com.example.android_development_practices.feature.profile.domain.ReminderPermissionException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidClassReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ClassReminderScheduler {
    private val alarms = context.getSystemService(AlarmManager::class.java)

    override fun access(): ReminderAccess {
        ClassReminderNotifications.createChannel(context)
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return ReminderAccess.NOTIFICATIONS
        }
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return ReminderAccess.NOTIFICATION_SETTINGS
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = context.getSystemService(android.app.NotificationManager::class.java)
                .getNotificationChannel(ClassReminderNotifications.CHANNEL_ID)
            if (channel?.importance == android.app.NotificationManager.IMPORTANCE_NONE) return ReminderAccess.NOTIFICATION_SETTINGS
        }
        if (Build.VERSION.SDK_INT >= 31 && !alarms.canScheduleExactAlarms()) return ReminderAccess.EXACT_ALARMS
        return ReminderAccess.READY
    }

    override fun schedule(reminder: ClassReminder) {
        val permission = access()
        if (permission != ReminderAccess.READY) throw ReminderPermissionException(permission)
        val intent = Intent(context, ClassReminderReceiver::class.java).apply {
            action = ClassReminderReceiver.ACTION_REMIND
            putExtra(ClassReminderReceiver.EXTRA_OWNER_NAME, reminder.ownerName)
        }
        val operation = PendingIntent.getBroadcast(context, 7, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        try {
            alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.triggerAtMillis, operation)
        } catch (_: SecurityException) {
            throw ReminderPermissionException(ReminderAccess.EXACT_ALARMS)
        }
    }
}
