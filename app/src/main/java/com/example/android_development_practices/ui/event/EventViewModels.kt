package com.example.android_development_practices.ui.event

import androidx.lifecycle.SavedStateHandle
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.android_development_practices.domain.model.SpaceEvent
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
class EventListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    @ApplicationContext context: Context,
) : BaseListViewModel<SpaceEvent>(dispatcher, CatalogFilters.load(context, "Ближайшие события")) {

    override suspend fun loadData(filters: CatalogFilters): List<SpaceEvent> =
        useCases.upcomingEvents(
            filters.apiSearch,
            filters.value("agency__ids")?.toIntOrNull(),
            filters.value("year")?.toIntOrNull(),
            filters.value("type__ids")?.toIntOrNull(),
        )
}

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    private val detailsCache: FavoriteDetailsCache,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<SpaceEvent>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): SpaceEvent =
        detailsCache.load("event", id, SpaceEvent.serializer()) { useCases.event(id.toInt()) }
}
