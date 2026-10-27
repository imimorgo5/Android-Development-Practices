package com.example.android_development_practices.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_development_practices.data.local.FavoriteDao
import com.example.android_development_practices.data.local.FavoriteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.android_development_practices.domain.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

fun favoriteSnapshot(value: Any): FavoriteEntity = when (value) {
    is Launch -> FavoriteEntity("launch:${value.id}", "launch", value.name, value.providerName.orEmpty(), value.imageUrl, value.id, Json.encodeToString(value))
    is SpaceEvent -> FavoriteEntity("event:${value.id}", "event", value.name, value.location.orEmpty(), value.imageUrl, value.id.toString(), Json.encodeToString(value))
    is Expedition -> FavoriteEntity("expedition:${value.id}", "expedition", value.name, value.stationName.orEmpty(), value.patchImageUrl, value.id.toString(), Json.encodeToString(value))
    is Agency -> FavoriteEntity("agency:${value.id}", "agency", value.name, value.countries.joinToString(), value.imageUrl, value.id.toString(), Json.encodeToString(value))
    is Astronaut -> FavoriteEntity("astronaut:${value.id}", "astronaut", value.name, value.agencyName.orEmpty(), value.imageUrl, value.id.toString(), Json.encodeToString(value))
    else -> error("Unsupported favorite item: ${value::class.simpleName}")
}

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val dao: FavoriteDao) : ViewModel() {
    val favorites = dao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun toggle(item: FavoriteEntity) = viewModelScope.launch {
        if (dao.contains(item.key)) dao.remove(item.key) else dao.add(item)
    }
    fun remove(key: String) = viewModelScope.launch { dao.remove(key) }
}

@HiltViewModel
class FilterBadgeViewModel @Inject constructor(val cache: FilterBadgeCache) : ViewModel()
