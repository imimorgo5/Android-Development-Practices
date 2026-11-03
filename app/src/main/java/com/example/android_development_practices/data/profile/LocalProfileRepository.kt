package com.example.android_development_practices.data.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.AtomicFile
import androidx.core.content.FileProvider
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.domain.profile.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

@Serializable
private data class StoredProfile(
    val fullName: String = "", val avatarUri: String? = null,
    // Preserve the keys of profiles saved by earlier app versions.
    @SerialName("resumeUrl") val portfolioUrl: String = "",
    @SerialName("occupation") val displayName: String = "",
)

@Singleton
class LocalProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val io: CoroutineDispatcher,
) : ProfileRepository {
    private val state = MutableStateFlow<Profile?>(null)
    override val profile = state.filterNotNull()
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }
    private val storage get() = AtomicFile(File(context.filesDir, "profile.json"))
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(90, TimeUnit.SECONDS).followSslRedirects(false).build()

    override suspend fun load(): Profile = withContext(io) {
        mutex.withLock {
            val stored = try {
                storage.openRead().bufferedReader().use { json.decodeFromString<StoredProfile>(it.readText()) }
            } catch (e: java.io.FileNotFoundException) {
                if (storage.baseFile.exists()) throw e
                StoredProfile()
            }
            Profile(stored.fullName, stored.avatarUri, stored.portfolioUrl, stored.displayName)
                .also { state.value = it }
        }
    }

    override suspend fun save(profile: Profile): Unit = withContext(io) {
        mutex.withLock {
            val stored = StoredProfile(profile.fullName, profile.avatarUri, profile.portfolioUrl, profile.displayName)
            val output = storage.startWrite()
            try {
                output.write(json.encodeToString(stored).toByteArray(Charsets.UTF_8))
                storage.finishWrite(output)
            } catch (e: Exception) {
                storage.failWrite(output)
                throw e
            }
            state.value = profile
            File(context.filesDir, "avatars").listFiles()?.filter { file ->
                file.toURI().toString() != profile.avatarUri && Uri.fromFile(file).toString() != profile.avatarUri
            }?.forEach { it.delete() }
        }
    }

    override suspend fun createCameraPhoto(): String = withContext(io) {
        val directory = File(context.cacheDir, "camera").apply { mkdirs() }
        directory.listFiles()?.filter { System.currentTimeMillis() - it.lastModified() > 86_400_000 }?.forEach { it.delete() }
        contentUri(File.createTempFile("capture_", ".jpg", directory))
    }

    override suspend fun importAvatar(uri: String): String = withContext(io) {
        val directory = File(context.filesDir, "avatars").apply { mkdirs() }
        val target = File(directory, "${UUID.randomUUID()}.img")
        try {
            context.contentResolver.openInputStream(Uri.parse(uri)).use { input ->
                if (input == null) throw IOException("Не удалось прочитать фотографию")
                target.outputStream().use { output -> copyLimited(input, output, 20L * 1024 * 1024) }
            }
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(target.path, options)
            if (options.outWidth <= 0 || options.outHeight <= 0) throw IOException("Выбранный файл не является фотографией")
            Uri.fromFile(target).toString()
        } catch (e: Exception) {
            target.delete()
            throw e
        }
    }

    override suspend fun downloadPortfolio(url: String): PortfolioDocument = withContext(io) {
        validatePortfolioUrl(url)
        val directory = File(context.cacheDir, "portfolios").apply { mkdirs() }
        directory.listFiles()?.filter { System.currentTimeMillis() - it.lastModified() > 86_400_000 }?.forEach { it.delete() }
        val call = client.newCall(Request.Builder().url(url).build())
        val response = call.execute()
        response.use {
            if (!it.isSuccessful) throw IOException("Сервер вернул ошибку ${it.code}. Проверьте ссылку")
            val body = it.body ?: throw IOException("Сервер вернул пустой документ")
            val mime = body.contentType()?.toString()?.substringBefore(';')?.lowercase()
            val extension = when (mime) {
                "application/pdf" -> "pdf"
                "application/msword" -> "doc"
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
                "text/plain" -> "txt"
                "application/octet-stream", null -> when (it.request.url.encodedPath.substringAfterLast('.').lowercase()) {
                    "pdf" -> "pdf"
                    else -> throw IOException("Нужна прямая ссылка на PDF, DOC, DOCX или TXT")
                }
                else -> throw IOException("По ссылке находится страница или неподдерживаемый файл. Укажите прямую ссылку на документ")
            }
            if (body.contentLength() > 25L * 1024 * 1024) throw IOException("Файл портфолио должен быть не больше 25 МБ")
            val target = File(directory, "portfolio_${UUID.randomUUID()}.$extension")
            try {
                body.byteStream().use { input -> target.outputStream().use { output -> copyLimited(input, output, 25L * 1024 * 1024) } }
                if (target.length() == 0L) throw IOException("Сервер вернул пустой документ")
                if (extension == "pdf") {
                    val header = target.inputStream().use { input -> ByteArray(5).also { input.read(it) } }
                    if (!header.contentEquals("%PDF-".toByteArray())) throw IOException("Файл по ссылке не является PDF")
                }
                PortfolioDocument(contentUri(target), when (extension) {
                    "pdf" -> "application/pdf"
                    "doc" -> "application/msword"
                    "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    else -> "text/plain"
                })
            } catch (e: Exception) {
                target.delete()
                throw e
            }
        }
    }

    private suspend fun copyLimited(input: java.io.InputStream, output: java.io.OutputStream, limit: Long) {
        val buffer = ByteArray(8192)
        var total = 0L
        while (true) {
            currentCoroutineContext().ensureActive()
            val count = input.read(buffer)
            if (count < 0) break
            total += count
            if (total > limit) throw IOException("Файл слишком большой (максимум ${limit / 1024 / 1024} МБ)")
            output.write(buffer, 0, count)
        }
    }

    private fun contentUri(file: File) = FileProvider.getUriForFile(context, "${context.packageName}.profile.files", file).toString()
}
