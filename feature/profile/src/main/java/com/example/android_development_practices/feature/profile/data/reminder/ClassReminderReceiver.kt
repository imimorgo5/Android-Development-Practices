package com.example.android_development_practices.feature.profile.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ClassReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_REMIND) return
        val ownerName = intent.getStringExtra(EXTRA_OWNER_NAME)?.takeIf(String::isNotBlank) ?: return
        ClassReminderNotifications.show(context, ownerName)
    }

    companion object {
        const val ACTION_REMIND = "com.example.android_development_practices.REMIND_CLASS"
        const val EXTRA_OWNER_NAME = "owner_name"
    }
}
