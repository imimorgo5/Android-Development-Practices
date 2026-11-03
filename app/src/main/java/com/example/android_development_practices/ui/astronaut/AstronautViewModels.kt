package com.example.android_development_practices.ui.astronaut

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
class AstronautListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    settingsUseCases: CatalogSettingsUseCases,
) : BaseListViewModel<AstronautListItemUiModel>(dispatcher, settingsUseCases, CatalogId.ASTRONAUT, useCases) {
    override val filterSources: List<CatalogFilterSource<AstronautListItemUiModel>> = listOf(
        CatalogFilterSource("nationality", "Национальность") { astronauts ->
            astronauts.flatMap { astronaut ->
                astronaut.source.nationalityFilterValues.map { CatalogOption(it, it) }
            }
        },
        CatalogFilterSource("agency_ids", "Агентство") { astronauts ->
            astronauts.mapNotNull { astronaut ->
                val id = astronaut.source.agencyId ?: return@mapNotNull null
                val name = astronaut.source.agencyName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                CatalogOption(name, id.toString())
            }
        },
        CatalogFilterSource("status_ids", "Статус") { astronauts ->
            astronauts.mapNotNull { astronaut ->
                val id = astronaut.source.statusId ?: return@mapNotNull null
                val name = astronaut.source.statusName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                CatalogOption(name, id.toString())
            }
        },
    )

    init { retry() }

    override suspend fun loadData(filters: CatalogFilters): List<AstronautListItemUiModel> = useCases.astronauts(
        filters.apiSearch,
        filters.value("nationality"),
        filters.value("agency_ids")?.toIntOrNull(),
        filters.value("status_ids")?.toIntOrNull(),
    ).map { it.toListUiModel() }
}

@HiltViewModel
class AstronautDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<AstronautDetailUiModel>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    init { retry() }

    override suspend fun loadData(id: String): AstronautDetailUiModel =
        useCases.astronaut(id.toInt()).toDetailUiModel()
}
