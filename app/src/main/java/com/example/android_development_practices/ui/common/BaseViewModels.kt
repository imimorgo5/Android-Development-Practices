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

abstract class BaseListViewModel<T>(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<T>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<T>>> = _uiState.asStateFlow()

    init {
        load()
    }

    abstract suspend fun loadData(): List<T>

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = runCatching { withContext(ioDispatcher) { loadData() } }
                .fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { UiState.Error(it.localizedMessage ?: "Не удалось загрузить данные") },
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
                    onFailure = { UiState.Error(it.localizedMessage ?: "Не удалось загрузить данные") },
                )
        }
    }
}