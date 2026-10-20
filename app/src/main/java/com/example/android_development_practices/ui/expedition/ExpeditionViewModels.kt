package com.example.android_development_practices.ui.expedition

import androidx.lifecycle.SavedStateHandle
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.data.local.FavoriteDetailsCache
import com.example.android_development_practices.domain.SpaceUseCases
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import com.example.android_development_practices.ui.common.CatalogFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class ExpeditionListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    @ApplicationContext context: Context,
) : BaseListViewModel<Expedition>(dispatcher, CatalogFilters.load(context, "Экспедиции")) {

    override suspend fun loadData(filters: CatalogFilters): List<Expedition> =
        useCases.expeditions(
            filters.apiSearch,
            filters.value("crew__astronaut__agency")?.toIntOrNull(),
            filters.value("start__gte"),
            filters.value("end__lte"),
            filters.value("space_station")?.toIntOrNull(),
        )
}

@HiltViewModel
class ExpeditionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    private val detailsCache: FavoriteDetailsCache,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Expedition>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Expedition =
        detailsCache.load("expedition", id, Expedition.serializer()) { useCases.expedition(id.toInt()) }
}
