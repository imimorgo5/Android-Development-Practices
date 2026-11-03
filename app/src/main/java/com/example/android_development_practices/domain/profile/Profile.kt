package com.example.android_development_practices.domain.profile

import java.net.URI
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

data class Profile(
    val fullName: String = "",
    val avatarUri: String? = null,
    val portfolioUrl: String = "",
    val displayName: String = "",
    val favoriteClassTime: String = "",
)

data class PortfolioDocument(val uri: String, val mimeType: String)

interface ProfileRepository {
    val profile: Flow<Profile>
    suspend fun load(): Profile
    suspend fun save(profile: Profile)
    suspend fun importAvatar(uri: String): String
    suspend fun createCameraPhoto(): String
    suspend fun downloadPortfolio(url: String): PortfolioDocument
}

enum class ProfileValidationError {
    NAME_REQUIRED, NAME_TOO_LONG, DISPLAY_NAME_TOO_LONG, PORTFOLIO_URL_INVALID,
    TIME_REQUIRED, TIME_FORMAT_INVALID, TIME_PASSED,
}

class ProfileValidationException(val reason: ProfileValidationError) : IllegalArgumentException(reason.name)

class ProfileSavedWithoutReminderException(cause: Exception) : IllegalStateException(cause)

class ValidateProfile @Inject constructor() {
    operator fun invoke(profile: Profile): Profile {
        val normalized = profile.copy(
            fullName = profile.fullName.trim(),
            portfolioUrl = profile.portfolioUrl.trim(),
            displayName = profile.displayName.trim(),
        )
        classTimeError(normalized.favoriteClassTime)?.let { throw ProfileValidationException(it) }
        if (normalized.fullName.isBlank()) throw ProfileValidationException(ProfileValidationError.NAME_REQUIRED)
        if (normalized.fullName.length > 150) throw ProfileValidationException(ProfileValidationError.NAME_TOO_LONG)
        if (normalized.displayName.length > 150) throw ProfileValidationException(ProfileValidationError.DISPLAY_NAME_TOO_LONG)
        if (normalized.portfolioUrl.isNotEmpty()) validatePortfolioUrl(normalized.portfolioUrl)
        return normalized
    }
}

fun validatePortfolioUrl(url: String) {
    val uri = try { URI(url) } catch (_: Exception) { null }
    if (uri == null || uri.scheme != "https" || uri.host.isNullOrBlank() || uri.userInfo != null) {
        throw ProfileValidationException(ProfileValidationError.PORTFOLIO_URL_INVALID)
    }
}

class ObserveProfile @Inject constructor(private val repository: ProfileRepository) {
    operator fun invoke() = repository.profile
}
class LoadProfile @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke() = repository.load()
}
data class PreparedProfileReminder(val profile: Profile, val reminder: ClassReminder)

class PrepareProfileReminder @Inject constructor(
    private val validate: ValidateProfile,
    private val createReminder: CreateClassReminder,
) {
    operator fun invoke(profile: Profile): PreparedProfileReminder {
        val normalized = validate(profile)
        return PreparedProfileReminder(normalized, createReminder(normalized))
    }
}

class SaveProfile @Inject constructor(
    private val repository: ProfileRepository,
    private val prepare: PrepareProfileReminder,
    private val reminders: ClassReminderScheduler,
) {
    suspend operator fun invoke(profile: Profile) {
        currentCoroutineContext().ensureActive()
        val prepared = prepare(profile)
        val access = reminders.access()
        if (access != ReminderAccess.READY) throw ReminderPermissionException(access)
        // Once persistence starts, finish scheduling even if the editor is destroyed.
        withContext(NonCancellable) {
            repository.save(prepared.profile)
            try {
                reminders.schedule(prepared.reminder)
            } catch (error: kotlinx.coroutines.CancellationException) {
                throw error
            } catch (error: Exception) {
                throw ProfileSavedWithoutReminderException(error)
            }
        }
    }
}
class ImportAvatar @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(uri: String) = repository.importAvatar(uri)
}
class CreateCameraPhoto @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke() = repository.createCameraPhoto()
}
class DownloadPortfolio @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(url: String): PortfolioDocument {
        validatePortfolioUrl(url)
        return repository.downloadPortfolio(url)
    }
}
