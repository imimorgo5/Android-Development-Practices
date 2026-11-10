package com.example.android_development_practices.feature.profile.ui

import com.example.android_development_practices.feature.profile.domain.Profile
import com.example.android_development_practices.feature.profile.domain.ProfileValidationError
import com.example.android_development_practices.feature.profile.domain.ReminderAccess
import com.example.android_development_practices.feature.profile.domain.classTimeError

data class ProfileUiModel(
    val fullName: String,
    val displayName: String,
    val avatarUri: String?,
    val portfolioUrl: String,
    val portfolioText: String,
    val favoriteClassText: String?,
)

internal fun Profile.toProfileUiModel() = ProfileUiModel(
    fullName = fullName.ifBlank { "Ваш профиль" },
    displayName = displayName.ifBlank { "Исследователь космоса" },
    avatarUri = avatarUri,
    portfolioUrl = portfolioUrl,
    portfolioText = portfolioUrl.ifBlank { "Добавьте ссылку на документ в редакторе профиля" },
    favoriteClassText = favoriteClassTime.takeIf(String::isNotBlank)?.let { "Время любимой пары: $it" },
)

data class ProfileState(
    val profile: ProfileUiModel = Profile().toProfileUiModel(),
    val loading: Boolean = true,
    val downloading: Boolean = false,
    val error: String? = null,
    val loaded: Boolean = false,
) {
    val loadErrorMessage: String get() = error ?: "Не удалось загрузить профиль"
    val canDownload: Boolean get() = loaded && profile.portfolioUrl.isNotBlank() && !downloading
}

data class ClockSelection(val hour: Int, val minute: Int)

data class EditProfileState(
    val profile: Profile = Profile(),
    val loading: Boolean = true,
    val busy: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
    val loaded: Boolean = false,
    val requestingPermission: Boolean = false,
    val timeEdited: Boolean = false,
) {
    val loadErrorMessage: String get() = error ?: "Не удалось загрузить профиль"
    val timeError: String? get() = classTimeError(profile.favoriteClassTime)?.toUserMessage()
    val visibleTimeError: String? get() = timeError.takeIf { timeEdited }
    val timeSupportingText: String get() = visibleTimeError ?: "Одно напоминание на сегодня по времени устройства"
    val canSave: Boolean get() = loaded && !loading && !busy && !requestingPermission && timeError == null
}

internal fun ProfileValidationError.toUserMessage(): String = when (this) {
    ProfileValidationError.NAME_REQUIRED -> "Укажите ФИО"
    ProfileValidationError.NAME_TOO_LONG -> "ФИО: не более 150 символов"
    ProfileValidationError.DISPLAY_NAME_TOO_LONG -> "Позывной: не более 150 символов"
    ProfileValidationError.PORTFOLIO_URL_INVALID -> "Укажите прямую HTTPS-ссылку на файл портфолио"
    ProfileValidationError.TIME_REQUIRED -> "Укажите время любимой пары"
    ProfileValidationError.TIME_FORMAT_INVALID -> "Введите время в формате HH:mm, например 09:30"
    ProfileValidationError.TIME_PASSED -> "Это время сегодня уже прошло. Выберите время позже текущего"
}

internal fun ReminderAccess.deniedMessage(): String = when (this) {
    ReminderAccess.NOTIFICATIONS -> "Без разрешения на уведомления напоминание не появится. Разрешите уведомления в настройках приложения"
    ReminderAccess.NOTIFICATION_SETTINGS -> "Уведомления отключены. Разрешите уведомления приложения и канала «Время любимой пары» в настройках"
    ReminderAccess.EXACT_ALARMS -> "Для напоминания в выбранное время разрешите будильники и напоминания, затем нажмите «Готово» снова"
    ReminderAccess.READY -> "Не удалось проверить разрешения. Попробуйте сохранить профиль снова"
}
