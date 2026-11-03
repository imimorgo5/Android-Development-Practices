package com.example.android_development_practices.ui.expedition

import com.example.android_development_practices.ui.common.CatalogId
import com.example.android_development_practices.ui.common.CatalogFilterSource
import com.example.android_development_practices.ui.common.CatalogOption

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
class ExpeditionListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    settingsUseCases: CatalogSettingsUseCases,
) : BaseListViewModel<ExpeditionListItemUiModel>(dispatcher, settingsUseCases, CatalogId.EXPEDITION, useCases) {
    override val filterSources: List<CatalogFilterSource<ExpeditionListItemUiModel>> = listOf(
        CatalogFilterSource("crew__astronaut__agency", "Агентство") { emptyList() },
        CatalogFilterSource("start__gte", "Дата начала от", isDate = true) { expeditions ->
            expeditions.mapNotNull { it.source.start?.take(10)?.let { date -> CatalogOption(date, date) } }
        },
        CatalogFilterSource("end__lte", "Дата окончания до", isDate = true) { expeditions ->
            expeditions.mapNotNull { it.source.end?.take(10)?.let { date -> CatalogOption(date, date) } }
        },
        CatalogFilterSource("space_station", "Космическая станция") { expeditions ->
            expeditions.mapNotNull { expedition ->
                val id = expedition.source.stationId ?: return@mapNotNull null
                val name = expedition.source.stationName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                CatalogOption(name, id.toString())
            }
        },
    )

    init { retry() }

    override suspend fun loadData(filters: CatalogFilters): List<ExpeditionListItemUiModel> =
        useCases.expeditions(
            filters.apiSearch,
            filters.value("crew__astronaut__agency")?.toIntOrNull(),
            filters.value("start__gte"),
            filters.value("end__lte"),
            filters.value("space_station")?.toIntOrNull(),
        ).map { it.toListUiModel() }
}

@HiltViewModel
class ExpeditionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<ExpeditionDetailUiModel>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    init { retry() }

    override suspend fun loadData(id: String): ExpeditionDetailUiModel =
        useCases.expedition(id.toInt()).toDetailUiModel()
}
