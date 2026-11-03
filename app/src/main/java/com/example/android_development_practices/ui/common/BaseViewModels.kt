package com.example.android_development_practices.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_development_practices.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import java.io.IOException
import retrofit2.HttpException
import kotlinx.serialization.SerializationException
import com.example.android_development_practices.domain.CatalogSettingsUseCases

abstract class BaseListViewModel<T>(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val settingsUseCases: CatalogSettingsUseCases,
    private val catalog: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<T>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<T>>> = _uiState.asStateFlow()

    private val _settings = MutableStateFlow(settingsUseCases.load(catalog))
    val settings = _settings.asStateFlow()
    private var currentFilters = _settings.value.filters
    private var loadJob: Job? = null

    init {
        load(currentFilters)
    }

    abstract suspend fun loadData(filters: CatalogFilters): List<T>

    fun retry() = load(currentFilters)

    fun applyFilters(filters: CatalogFilters) {
        settingsUseCases.saveFilters(catalog, filters)
        _settings.value = _settings.value.copy(filters = filters)
        currentFilters = filters
        load(filters)
    }

    fun saveFilterOptions(options: Map<String, List<CatalogOption>>) {
        settingsUseCases.saveOptions(catalog, options)
        _settings.value = _settings.value.copy(options = options)
    }

    private fun load(filters: CatalogFilters) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = runCatching { withContext(ioDispatcher) { loadData(filters) } }
                .fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { error ->
                        if (error is CancellationException) throw error
                        UiState.Error(error.toUserMessage())
                    },
                )
        }
    }
}

abstract class BaseDetailViewModel<T>(
    protected val argId: String,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    init {
        load()
    }

    protected abstract suspend fun loadData(id: String): T

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = runCatching { withContext(ioDispatcher) { loadData(argId) } }
                .fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { error ->
                        if (error is CancellationException) throw error
                        UiState.Error(error.toUserMessage())
                    },
                )
        }
    }
}

private fun Throwable.toUserMessage(): String = when (this) {
    is IOException -> "Не удалось связаться с сервером. Проверьте подключение и повторите попытку."
    is SerializationException -> "Сервер вернул данные в неожиданном формате. Попробуйте позже."
    is HttpException -> when (code()) {
        404 -> "Запрошенная запись не найдена."
        429 -> "Слишком много запросов к API. Попробуйте позже или переключите API на сервер разработки."
        in 500..599 -> "Сервер временно недоступен (${code()}). Попробуйте позже."
        else -> "Ошибка сервера (${code()}). Попробуйте ещё раз."
    }
    else -> localizedMessage?.takeIf { it.isNotBlank() } ?: "Не удалось загрузить данные. Попробуйте ещё раз."
}
