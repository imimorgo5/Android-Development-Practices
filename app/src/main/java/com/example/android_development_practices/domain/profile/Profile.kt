package com.example.android_development_practices.domain.profile

import java.net.URI
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

data class Profile(
    val fullName: String = "",
    val avatarUri: String? = null,
    val portfolioUrl: String = "",
    val displayName: String = "",
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

class ProfileValidationException(message: String) : IllegalArgumentException(message)

class ValidateProfile @Inject constructor() {
    operator fun invoke(profile: Profile): Profile {
        val normalized = profile.copy(
            fullName = profile.fullName.trim(),
            portfolioUrl = profile.portfolioUrl.trim(),
            displayName = profile.displayName.trim(),
        )
        if (normalized.fullName.isBlank()) throw ProfileValidationException("Укажите ФИО")
        if (normalized.fullName.length > 150) throw ProfileValidationException("ФИО: не более 150 символов")
        if (normalized.displayName.length > 150) throw ProfileValidationException("Позывной: не более 150 символов")
        if (normalized.portfolioUrl.isNotEmpty()) validatePortfolioUrl(normalized.portfolioUrl)
        return normalized
    }
}

fun validatePortfolioUrl(url: String) {
    val uri = try { URI(url) } catch (_: Exception) { null }
    if (uri == null || uri.scheme != "https" || uri.host.isNullOrBlank() || uri.userInfo != null) {
        throw ProfileValidationException("Укажите прямую HTTPS-ссылку на файл портфолио")
    }
}

class ObserveProfile @Inject constructor(private val repository: ProfileRepository) {
    operator fun invoke() = repository.profile
}
class LoadProfile @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke() = repository.load()
}
class SaveProfile @Inject constructor(
    private val repository: ProfileRepository,
    private val validate: ValidateProfile,
) {
    suspend operator fun invoke(profile: Profile) = repository.save(validate(profile))
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
