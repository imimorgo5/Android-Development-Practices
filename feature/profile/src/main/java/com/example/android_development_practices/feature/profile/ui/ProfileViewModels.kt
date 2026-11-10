package com.example.android_development_practices.feature.profile.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_development_practices.feature.profile.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal fun profileError(error: Exception): String = when (error) {
    is java.net.UnknownHostException, is java.net.ConnectException -> "Нет подключения к интернету. Проверьте сеть и повторите попытку"
    is java.net.SocketTimeoutException -> "Сервер не отвечает. Повторите попытку позже"
    is ReminderPermissionException -> error.access.deniedMessage()
    is ProfileSavedWithoutReminderException -> "Профиль сохранён, но напоминание не назначено. Проверьте разрешения и сохраните профиль снова"
    is SecurityException -> "Нет доступа к файлу. Выберите фотографию заново"
    is ProfileValidationException -> error.reason.toUserMessage()
    is IOException -> error.message ?: "Не удалось прочитать или записать файл. Проверьте свободное место и повторите попытку"
    else -> "Не удалось выполнить действие. Повторите попытку"
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val load: LoadProfile,
    observe: ObserveProfile,
    private val download: DownloadPortfolio,
) : ViewModel() {
    private val mutableState = MutableStateFlow(ProfileState())
    val state = mutableState.asStateFlow()
    private val documents = Channel<PortfolioDocument>(Channel.BUFFERED)
    val openDocuments = documents.receiveAsFlow()

    init {
        viewModelScope.launch { observe().collect { profile -> mutableState.update { it.copy(profile = profile.toProfileUiModel()) } } }
        reload()
    }

    fun reload() {
        mutableState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val profile = load()
                mutableState.update { it.copy(profile = profile.toProfileUiModel(), loading = false, loaded = true) }
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) { mutableState.update { it.copy(loading = false, error = profileError(e)) } }
        }
    }

    fun downloadPortfolio() {
        if (state.value.downloading || state.value.profile.portfolioUrl.isBlank()) return
        mutableState.update { it.copy(downloading = true, error = null) }
        viewModelScope.launch {
            try { documents.send(download(state.value.profile.portfolioUrl))
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) { showError(profileError(e))
            } finally { mutableState.update { it.copy(downloading = false) } }
        }
    }
    fun showError(message: String) { mutableState.update { it.copy(error = message) } }
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val load: LoadProfile,
    private val save: SaveProfile,
    private val importAvatar: ImportAvatar,
    private val createCameraPhoto: CreateCameraPhoto,
    private val getReminderAccess: GetReminderAccess,
    private val prepareReminder: PrepareProfileReminder,
    private val clock: ReminderClock,
) : ViewModel() {
    private val mutableState = MutableStateFlow(EditProfileState())
    val state = mutableState.asStateFlow()
    private val captures = Channel<String>(Channel.BUFFERED)
    val cameraRequests = captures.receiveAsFlow()
    private val permissions = Channel<ReminderAccess>(Channel.BUFFERED)
    val reminderPermissions = permissions.receiveAsFlow()
    var pendingCameraUri: String?
        get() = handle["cameraUri"]
        private set(value) { handle["cameraUri"] = value }

    init { reload() }

    fun reload() {
        mutableState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val stored = load()
                val profile = if (handle.get<Boolean>("draft") == true) Profile(
                    handle["name"] ?: stored.fullName, handle["avatar"] ?: stored.avatarUri,
                    handle["portfolio"] ?: stored.portfolioUrl, handle["displayName"] ?: stored.displayName,
                    handle["favoriteClassTime"] ?: stored.favoriteClassTime,
                ) else stored
                mutableState.value = EditProfileState(
                    profile = profile, loading = false, loaded = true,
                    timeEdited = handle["timeEdited"] ?: false,
                    requestingPermission = handle.get<String>("pendingReminderPermission") != null,
                    error = state.value.error,
                )
                handle.get<String>("selectedAvatar")?.let(::selectAvatar)
                resumeSaveAfterPermission()
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) { mutableState.update { it.copy(loading = false, error = profileError(e)) } }
        }
    }
    fun updateName(value: String) = update(state.value.profile.copy(fullName = value))
    fun updatePortfolio(value: String) = update(state.value.profile.copy(portfolioUrl = value))
    fun updateDisplayName(value: String) = update(state.value.profile.copy(displayName = value))
    fun setClassTime(hour: Int, minute: Int) = updateClassTime(String.format(java.util.Locale.ROOT, "%02d:%02d", hour, minute))
    fun clockSelection(): ClockSelection {
        val time = state.value.profile.favoriteClassTime
        if (classTimeError(time) == null) {
            val (hour, minute) = time.split(":").map(String::toInt)
            return ClockSelection(hour, minute)
        }
        val now = java.util.Calendar.getInstance().apply { timeInMillis = clock.nowMillis() }
        return ClockSelection(now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE))
    }
    fun reminderPermissionResult(requested: ReminderAccess, granted: Boolean) {
        handle.remove<String>("pendingReminderPermission")
        mutableState.update { it.copy(requestingPermission = false) }
        if (requested == ReminderAccess.NOTIFICATIONS && !granted) handle["notificationsDenied"] = true
        handle["resumeSaveAfterPermission"] = granted
        if (granted) resumeSaveAfterPermission() else showError(requested.deniedMessage())
    }
    private fun resumeSaveAfterPermission() {
        if (handle.get<Boolean>("resumeSaveAfterPermission") != true) return
        val current = state.value
        if (current.loading || !current.loaded || current.busy || current.requestingPermission) return
        handle.remove<Boolean>("resumeSaveAfterPermission")
        save()
    }
    fun reminderSettingsReturned(requested: ReminderAccess) {
        val access = getReminderAccess()
        val granted = when (requested) {
            ReminderAccess.NOTIFICATIONS, ReminderAccess.NOTIFICATION_SETTINGS ->
                access != ReminderAccess.NOTIFICATIONS && access != ReminderAccess.NOTIFICATION_SETTINGS
            ReminderAccess.EXACT_ALARMS -> access == ReminderAccess.READY
            ReminderAccess.READY -> true
        }
        reminderPermissionResult(requested, granted)
    }
    fun reminderPermissionRequestFailed() {
        handle.remove<String>("pendingReminderPermission")
        handle.remove<Boolean>("resumeSaveAfterPermission")
        mutableState.update { it.copy(requestingPermission = false) }
        showError("Не удалось открыть запрос разрешения. Разрешите уведомления и напоминания в настройках приложения")
    }
    fun updateClassTime(value: String) {
        handle["timeEdited"] = true
        mutableState.update { it.copy(timeEdited = true) }
        update(state.value.profile.copy(favoriteClassTime = value))
    }
    private fun update(profile: Profile) {
        handle["draft"] = true
        handle["name"] = profile.fullName
        handle["avatar"] = profile.avatarUri
        handle["portfolio"] = profile.portfolioUrl
        handle["displayName"] = profile.displayName
        handle["favoriteClassTime"] = profile.favoriteClassTime
        mutableState.update { it.copy(profile = profile, error = null) }
    }
    fun selectAvatar(uri: String) {
        handle["selectedAvatar"] = uri
        if (state.value.loading || !state.value.loaded) return
        runAction {
            update(state.value.profile.copy(avatarUri = importAvatar(uri)))
            handle.remove<String>("selectedAvatar")
        }
    }
    fun takePhoto() = runAction {
        val uri = createCameraPhoto()
        pendingCameraUri = uri
        captures.send(uri)
    }
    fun photoTaken(success: Boolean) {
        val uri = pendingCameraUri
        pendingCameraUri = null
        if (success && uri != null) selectAvatar(uri)
    }
    fun save() {
        if (!state.value.canSave) return
        try {
            prepareReminder(state.value.profile)
        } catch (error: ProfileValidationException) {
            showError(profileError(error))
            return
        }
        val access = getReminderAccess()
        if (access != ReminderAccess.READY) {
            val requested = if (access == ReminderAccess.NOTIFICATIONS && handle.get<Boolean>("notificationsDenied") == true) {
                ReminderAccess.NOTIFICATION_SETTINGS
            } else access
            handle["pendingReminderPermission"] = requested.name
            mutableState.update { it.copy(requestingPermission = true, error = null) }
            permissions.trySend(requested)
            return
        }
        runAction {
            save(state.value.profile)
            mutableState.update { it.copy(saved = true) }
        }
    }
    fun showError(message: String) { mutableState.update { it.copy(error = message) } }
    private fun runAction(action: suspend () -> Unit) {
        if (state.value.busy || state.value.loading || !state.value.loaded) return
        mutableState.update { it.copy(busy = true, error = null) }
        viewModelScope.launch {
            try { action()
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) { showError(profileError(e))
            } finally {
                mutableState.update { it.copy(busy = false) }
                resumeSaveAfterPermission()
            }
        }
    }
}
