package com.example.android_development_practices.domain.profile

import java.util.Calendar
import javax.inject.Inject

fun classTimeError(value: String): ProfileValidationError? = when {
    value.isEmpty() -> ProfileValidationError.TIME_REQUIRED
    !Regex("([01][0-9]|2[0-3]):[0-5][0-9]").matches(value) -> ProfileValidationError.TIME_FORMAT_INVALID
    else -> null
}

data class ClassReminder(val ownerName: String, val triggerAtMillis: Long)

enum class ReminderAccess { NOTIFICATIONS, NOTIFICATION_SETTINGS, EXACT_ALARMS, READY }

interface ClassReminderScheduler {
    fun access(): ReminderAccess
    fun schedule(reminder: ClassReminder)
}

class ReminderPermissionException(val access: ReminderAccess) : IllegalStateException(access.name)

class GetReminderAccess @Inject constructor(private val scheduler: ClassReminderScheduler) {
    operator fun invoke(): ReminderAccess = scheduler.access()
}

fun interface ReminderClock {
    fun nowMillis(): Long
}

class CreateClassReminder @Inject constructor(private val clock: ReminderClock) {
    operator fun invoke(profile: Profile): ClassReminder {
        val nowMillis = clock.nowMillis()
        classTimeError(profile.favoriteClassTime)?.let { throw ProfileValidationException(it) }
        val (hour, minute) = profile.favoriteClassTime.split(":").map(String::toInt)
        val time = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        if (time <= nowMillis) throw ProfileValidationException(ProfileValidationError.TIME_PASSED)
        return ClassReminder(profile.fullName.trim(), time)
    }
}
