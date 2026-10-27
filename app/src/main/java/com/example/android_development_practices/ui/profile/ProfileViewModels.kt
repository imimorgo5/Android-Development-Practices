package com.example.android_development_practices.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_development_practices.domain.profile.*
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
    is SecurityException -> "Нет доступа к файлу. Выберите фотографию заново"
    is ProfileValidationException -> error.message.orEmpty()
    is IOException -> error.message ?: "Не удалось прочитать или записать файл. Проверьте свободное место и повторите попытку"
    else -> "Не удалось выполнить действие. Повторите попытку"
}

data class ProfileState(
    val profile: Profile = Profile(), val loading: Boolean = true,
    val downloading: Boolean = false, val error: String? = null, val loaded: Boolean = false,
)

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
        viewModelScope.launch { observe().collect { profile -> mutableState.update { it.copy(profile = profile) } } }
        reload()
    }

    fun reload() {
        mutableState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val profile = load()
                mutableState.update { it.copy(profile = profile, loading = false, loaded = true) }
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

data class EditProfileState(
    val profile: Profile = Profile(), val loading: Boolean = true,
    val busy: Boolean = false, val saved: Boolean = false, val error: String? = null,
    val loaded: Boolean = false,
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val load: LoadProfile,
    private val save: SaveProfile,
    private val importAvatar: ImportAvatar,
    private val createCameraPhoto: CreateCameraPhoto,
) : ViewModel() {
    private val mutableState = MutableStateFlow(EditProfileState())
    val state = mutableState.asStateFlow()
    private val captures = Channel<String>(Channel.BUFFERED)
    val cameraRequests = captures.receiveAsFlow()
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
                ) else stored
                mutableState.value = EditProfileState(profile = profile, loading = false, loaded = true)
                handle.get<String>("selectedAvatar")?.let(::selectAvatar)
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) { mutableState.update { it.copy(loading = false, error = profileError(e)) } }
        }
    }
    fun updateName(value: String) = update(state.value.profile.copy(fullName = value))
    fun updatePortfolio(value: String) = update(state.value.profile.copy(portfolioUrl = value))
    fun updateDisplayName(value: String) = update(state.value.profile.copy(displayName = value))
    private fun update(profile: Profile) {
        handle["draft"] = true
        handle["name"] = profile.fullName
        handle["avatar"] = profile.avatarUri
        handle["portfolio"] = profile.portfolioUrl
        handle["displayName"] = profile.displayName
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
    fun save() = runAction {
        save(state.value.profile)
        mutableState.update { it.copy(saved = true) }
    }
    fun showError(message: String) { mutableState.update { it.copy(error = message) } }
    private fun runAction(action: suspend () -> Unit) {
        if (state.value.busy || state.value.loading || !state.value.loaded) return
        mutableState.update { it.copy(busy = true, error = null) }
        viewModelScope.launch {
            try { action()
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) { showError(profileError(e))
            } finally { mutableState.update { it.copy(busy = false) } }
        }
    }
}
