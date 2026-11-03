package com.example.android_development_practices.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.android_development_practices.domain.FavoriteUseCases
import com.example.android_development_practices.domain.model.FavoriteSource
import com.example.android_development_practices.domain.model.favoriteKey

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val useCases: FavoriteUseCases) : ViewModel() {
    val favorites = useCases.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun key(item: FavoriteSource): String = item.favoriteKey
    fun toggle(item: FavoriteSource) = viewModelScope.launch { useCases.toggle(item) }
    fun remove(key: String) = viewModelScope.launch { useCases.remove(key) }
}

@HiltViewModel
class FilterBadgeViewModel @Inject constructor(val cache: FilterBadgeCache) : ViewModel()
