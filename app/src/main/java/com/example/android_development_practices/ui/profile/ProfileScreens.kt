package com.example.android_development_practices.ui.profile

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.android_development_practices.ui.common.AppTextFieldDefaults
import com.example.android_development_practices.ui.common.ScreenScaffold

@Composable
fun ProfileRoute(onEdit: () -> Unit, vm: ProfileViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(vm) {
        vm.openDocuments.collect { document ->
            try {
                val uri = Uri.parse(document.uri)
                context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, document.mimeType)
                    clipData = ClipData.newRawUri("Портфолио", uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                })
            } catch (_: ActivityNotFoundException) {
                vm.showError("Документ скачан. Установите приложение для просмотра PDF/документов и нажмите «Портфолио» снова")
            } catch (_: SecurityException) {
                vm.showError("Просмотрщик не смог открыть документ. Попробуйте другое приложение")
            }
        }
    }
    ScreenScaffold(title = "Профиль", onBack = null) {
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (!state.loaded) {
            Column(Modifier.padding(20.dp)) {
                ErrorMessage(state.error ?: "Не удалось загрузить профиль", vm::reload)
            }
        } else Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileAvatar(state.profile.avatarUri)
                    Text(state.profile.fullName.ifBlank { "Ваш профиль" }, style = MaterialTheme.typography.headlineSmall)
                    Text(state.profile.displayName.ifBlank { "Исследователь космоса" },
                        style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Редактировать")
                    }
                }
            }
            Text("Личные данные", style = MaterialTheme.typography.titleLarge)
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainer) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Портфолио", style = MaterialTheme.typography.titleMedium)
                    Text(state.profile.portfolioUrl.ifBlank { "Добавьте ссылку на документ в редакторе профиля" },
                        style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = vm::downloadPortfolio,
                        enabled = state.profile.portfolioUrl.isNotBlank() && !state.downloading,
                        modifier = Modifier.fillMaxWidth()) {
                        if (state.downloading) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(if (state.downloading) "Скачивание…" else "Портфолио")
                    }
                }
            }
            state.error?.let { ErrorMessage(it) }
        }
    }
}

@Composable
fun EditProfileRoute(onBack: () -> Unit, vm: EditProfileViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    var photoDialog by rememberSaveable { mutableStateOf(false) }
    var permissionChecked by rememberSaveable { mutableStateOf(false) }
    val storagePermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            Toast.makeText(context, "Доступ к фотографиям запрещён. Редактор закрыт", Toast.LENGTH_LONG).show()
            onBack()
        }
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { vm.selectAvatar(it.toString()) }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture(), vm::photoTaken)
    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) vm.takePhoto()
        else vm.showError("Доступ к камере запрещён. Разрешите его в настройках приложения или выберите фото из галереи")
    }
    LaunchedEffect(Unit) {
        if (!permissionChecked) {
            permissionChecked = true
            if (Build.VERSION.SDK_INT <= 32 && ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                storagePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    LaunchedEffect(state.saved) { if (state.saved) onBack() }
    LaunchedEffect(vm) {
        vm.cameraRequests.collect { uri ->
            try { camera.launch(Uri.parse(uri))
            } catch (_: ActivityNotFoundException) { vm.photoTaken(false); vm.showError("На устройстве нет приложения камеры")
            } catch (_: SecurityException) { vm.photoTaken(false); vm.showError("Камера недоступна. Проверьте разрешения приложения") }
        }
    }
    BackHandler(enabled = state.busy) { /* Finish the file operation before leaving. */ }
    ScreenScaffold(title = "Редактирование профиля", onBack = { if (!state.busy) onBack() }) {
        if (state.loading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else if (!state.loaded) {
            Column(Modifier.padding(20.dp)) {
                ErrorMessage(state.error ?: "Не удалось загрузить профиль", vm::reload)
            }
        } else Column(
            Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(onClick = { photoDialog = true }, enabled = !state.busy,
                shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                ProfileAvatar(state.profile.avatarUri)
            }
            TextButton(onClick = { photoDialog = true }, enabled = !state.busy) { Text("Изменить фотографию", style = MaterialTheme.typography.titleMedium) }
            OutlinedTextField(state.profile.fullName, vm::updateName, label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth(), enabled = !state.busy, singleLine = true,
                shape = AppTextFieldDefaults.shape, colors = AppTextFieldDefaults.colors(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
            OutlinedTextField(state.profile.displayName, vm::updateDisplayName, label = { Text("Позывной (необязательно)") },
                modifier = Modifier.fillMaxWidth(), enabled = !state.busy, singleLine = true,
                shape = AppTextFieldDefaults.shape, colors = AppTextFieldDefaults.colors())
            OutlinedTextField(state.profile.portfolioUrl, vm::updatePortfolio, label = { Text("Ссылка на портфолио (необязательно)") },
                supportingText = { Text("Прямая HTTPS-ссылка на PDF, DOC, DOCX или TXT, до 25 МБ") },
                modifier = Modifier.fillMaxWidth(), enabled = !state.busy, singleLine = true,
                shape = AppTextFieldDefaults.shape, colors = AppTextFieldDefaults.colors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri))
            state.error?.let { ErrorMessage(it) }
            Button(onClick = vm::save, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                if (state.busy) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (state.busy) "Подождите…" else "Готово")
            }
        }
    }
    if (photoDialog) AlertDialog(
        onDismissRequest = { photoDialog = false },
        title = { Text("Фотография профиля") },
        text = { Text("Выберите фотографию на устройстве или сделайте новый снимок") },
        confirmButton = { TextButton(onClick = {
            photoDialog = false
            try { gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } catch (_: ActivityNotFoundException) { vm.showError("На устройстве нет приложения для выбора фотографий") }
        }) { Text("Галерея") } },
        dismissButton = { TextButton(onClick = {
            photoDialog = false
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) vm.takePhoto()
            else cameraPermission.launch(Manifest.permission.CAMERA)
        }) { Text("Камера") } },
    )
}

@Composable
private fun ProfileAvatar(uri: String?) {
    SubcomposeAsyncImage(
        model = uri, contentDescription = "Фотография профиля", contentScale = ContentScale.Crop,
        modifier = Modifier.size(112.dp).clip(CircleShape),
        loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(Modifier.size(24.dp)) } },
        error = { Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = "Фото не выбрано или недоступно", Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        } },
    )
}

@Composable
private fun ErrorMessage(message: String, onRetry: (() -> Unit)? = null) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.errorContainer) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(message, color = MaterialTheme.colorScheme.onErrorContainer)
            if (onRetry != null) TextButton(onClick = onRetry) { Text("Повторить загрузку профиля") }
        }
    }
}
