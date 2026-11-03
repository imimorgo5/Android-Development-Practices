package com.example.android_development_practices.ui.event

import com.example.android_development_practices.ui.common.CatalogId
import com.example.android_development_practices.ui.common.CatalogFilterSource
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.upcomingYearOptions

import androidx.lifecycle.SavedStateHandle
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
) : BaseListViewModel<EventListItemUiModel>(dispatcher, settingsUseCases, CatalogId.EVENT, useCases) {
    override val filterSources: List<CatalogFilterSource<EventListItemUiModel>> = listOf(
        CatalogFilterSource("agency__ids", "Агентство") { emptyList() },
        CatalogFilterSource("year", "Год") { events ->
            upcomingYearOptions(events.mapNotNull { it.source.date?.take(4)?.takeIf { year -> year.toIntOrNull() != null } })
        },
        CatalogFilterSource("type__ids", "Тип события") { events ->
            events.mapNotNull { event ->
                val id = event.source.typeId ?: return@mapNotNull null
                val name = event.source.typeName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                CatalogOption(name, id.toString())
            }
        },
    )

    init { retry() }

    override suspend fun loadData(filters: CatalogFilters): List<EventListItemUiModel> =
        useCases.upcomingEvents(
            filters.apiSearch,
            filters.value("agency__ids")?.toIntOrNull(),
            filters.value("year")?.toIntOrNull(),
            filters.value("type__ids")?.toIntOrNull(),
        ).map { it.toListUiModel() }
}

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<EventDetailUiModel>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    init { retry() }

    override suspend fun loadData(id: String): EventDetailUiModel =
        useCases.event(id.toInt()).toDetailUiModel()
}
