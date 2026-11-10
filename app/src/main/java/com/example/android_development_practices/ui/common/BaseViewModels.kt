package com.example.android_development_practices.ui.common

import com.example.android_development_practices.domain.model.CatalogSettings
import com.example.android_development_practices.domain.SpaceUseCases
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
    private val catalog: CatalogId,
    private val useCases: SpaceUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<T>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<T>>> = _uiState.asStateFlow()

    private val _settings = MutableStateFlow<CatalogSettings?>(null)
    val settings = _settings.asStateFlow()
    private var currentFilters = CatalogFilters()
    private var filtersNeedSaving = false
    private var loadJob: Job? = null
    private var cacheJob: Job? = null

    protected abstract val filterSources: List<CatalogFilterSource<T>>
    val filterDefinitions: List<CatalogFilterDefinition> get() = filterSources.map { it.definition }
    private val _filterOptions = MutableStateFlow<Map<String, List<CatalogOption>>>(emptyMap())
    val filterOptions = _filterOptions.asStateFlow()
    private var itemOptions: Map<String, List<CatalogOption>> = emptyMap()
    private var apiOptions: Map<String, List<CatalogOption>> = emptyMap()
    private var optionsLoaded = false
    private var hasItems = false

    abstract suspend fun loadData(filters: CatalogFilters): List<T>

    fun retry() = load()

    fun applyFilters(filters: CatalogFilters) {
        currentFilters = filters
        _settings.value = _settings.value?.copy(filters = filters)
        filtersNeedSaving = true
        load()
    }

    fun loadFilterOptions() {
        if (optionsLoaded) return
        optionsLoaded = true
        viewModelScope.launch {
            try {
                apiOptions = withContext(ioDispatcher) { useCases.catalogFilterOptions(catalog.apiKey) }
                publishFilterOptions()
            } catch (error: CancellationException) {
                optionsLoaded = false
                throw error
            } catch (_: Exception) {
                optionsLoaded = false
            }
        }
    }

    private fun publishFilterOptions() {
        val settings = _settings.value ?: return
        val currentOptions = filterDefinitions.associate { definition ->
            definition.key to apiOptions[definition.key].orEmpty().ifEmpty { itemOptions[definition.key].orEmpty() }
                .distinctBy(CatalogOption::value).sortedBy(CatalogOption::label)
        }
        if (hasItems && currentFilters.query.isBlank() && currentFilters.params.values.none(String::isNotBlank)) {
            if (currentOptions != settings.options) {
                _settings.value = _settings.value?.copy(options = currentOptions)
                cacheJob?.cancel()
                cacheJob = viewModelScope.launch {
                    try {
                        settingsUseCases.saveOptions(catalog.settingsKey, currentOptions)
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Exception) {
                        android.util.Log.w("CatalogFilters", "Failed to persist filter options", error)
                    }
                }
            }
        }
        _filterOptions.value = filterDefinitions.associate { definition ->
            val key = definition.key
            key to apiOptions[key].orEmpty().ifEmpty {
                _settings.value?.options?.get(key).orEmpty().ifEmpty { currentOptions[key].orEmpty() }
            }.distinctBy(CatalogOption::value).sortedBy(CatalogOption::label)
        }
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            hasItems = false
            try {
                if (_settings.value == null) {
                    val restored = settingsUseCases.load(catalog.settingsKey)
                    if (!filtersNeedSaving) currentFilters = restored.filters
                    _settings.value = restored.copy(filters = currentFilters)
                    _filterOptions.value = restored.options
                }
                val filters = currentFilters
                if (filtersNeedSaving) {
                    settingsUseCases.saveFilters(catalog.settingsKey, filters)
                    if (currentFilters == filters) filtersNeedSaving = false
                }
                val items = withContext(ioDispatcher) { loadData(filters) }
                itemOptions = filterSources.associate { it.key to it.options(items) }
                hasItems = true
                publishFilterOptions()
                _uiState.value = UiState.Success(items)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = UiState.Error(error.toUserMessage())
            }
        }
    }
}

abstract class BaseDetailViewModel<T>(
    protected val argId: String,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    protected abstract suspend fun loadData(id: String): T

    fun retry() = load()

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
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
