package com.example.android_development_practices.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.domain.SpaceUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

@HiltViewModel
class ApiCatalogOptionsViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val mutableOptions = MutableStateFlow<Map<String, List<CatalogOption>>>(emptyMap())
    val options = mutableOptions.asStateFlow()
    private val loadedCatalogs = mutableSetOf<String>()

    fun loadOptions(catalog: String) {
        if (!loadedCatalogs.add(catalog)) return
        viewModelScope.launch {
            val result = runCatching {
                withContext(ioDispatcher) { useCases.catalogFilterOptions(catalog) }
                    .mapValues { (_, options) -> options.map { CatalogOption(it.label, it.value) } }
            }
            result.onSuccess { loaded -> mutableOptions.value = mutableOptions.value + loaded }
            result.onFailure { error ->
                loadedCatalogs.remove(catalog)
                if (error is CancellationException) throw error
            }
        }
    }
}
