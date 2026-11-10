package com.example.android_development_practices.ui.launch

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
class LaunchListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    settingsUseCases: CatalogSettingsUseCases,
) : BaseListViewModel<LaunchListItemUiModel>(dispatcher, settingsUseCases, CatalogId.LAUNCH, useCases) {
    override val filterSources: List<CatalogFilterSource<LaunchListItemUiModel>> = listOf(
        CatalogFilterSource("lsp__id", "Агентство") { launches ->
            launches.mapNotNull { launch ->
                val id = launch.source.providerId ?: return@mapNotNull null
                val name = launch.source.providerName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                CatalogOption(name, id.toString())
            }
        },
        CatalogFilterSource("year", "Год") { launches ->
            upcomingYearOptions(launches.mapNotNull { it.source.net?.take(4)?.takeIf { year -> year.toIntOrNull() != null } })
        },
        CatalogFilterSource("status__ids", "Статус") { launches ->
            launches.mapNotNull { launch ->
                val id = launch.source.statusId ?: return@mapNotNull null
                val name = launch.source.status?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                CatalogOption(name, id.toString())
            }
        },
    )

    init { retry() }

    override suspend fun loadData(filters: CatalogFilters): List<LaunchListItemUiModel> = useCases.upcomingLaunches(
        filters.apiSearch,
        filters.value("lsp__id")?.toIntOrNull(),
        filters.value("year")?.toIntOrNull(),
        filters.value("status__ids")?.toIntOrNull(),
    ).map { it.toListUiModel() }
}

@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<LaunchDetailUiModel>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    init { retry() }

    override suspend fun loadData(id: String): LaunchDetailUiModel = useCases.launch(id).toDetailUiModel()
}
