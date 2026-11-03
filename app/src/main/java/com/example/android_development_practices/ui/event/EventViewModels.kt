package com.example.android_development_practices.ui.event

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.domain.model.SpaceEvent
import com.example.android_development_practices.domain.SpaceUseCases
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import com.example.android_development_practices.ui.common.CatalogFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import com.example.android_development_practices.domain.CatalogSettingsUseCases

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    settingsUseCases: CatalogSettingsUseCases,
) : BaseListViewModel<SpaceEvent>(dispatcher, settingsUseCases, "Ближайшие события") {

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
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<SpaceEvent>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): SpaceEvent =
        useCases.event(id.toInt())
}
