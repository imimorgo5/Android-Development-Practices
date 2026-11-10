package com.example.android_development_practices.ui.agency

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
class AgencyListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    settingsUseCases: CatalogSettingsUseCases,
) : BaseListViewModel<AgencyListItemUiModel>(dispatcher, settingsUseCases, CatalogId.AGENCY, useCases) {
    override val filterSources: List<CatalogFilterSource<AgencyListItemUiModel>> = listOf(
        CatalogFilterSource("country_code", "Страна") { agencies ->
            agencies.flatMap { agency -> agency.source.countryFilterValues.map { CatalogOption(it.name, it.alpha3Code) } }
        },
        CatalogFilterSource("type__id", "Тип агентства") { agencies ->
            agencies.mapNotNull { agency ->
                val id = agency.source.typeId ?: return@mapNotNull null
                val name = agency.source.typeName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                CatalogOption(name, id.toString())
            }
        },
    )

    init { retry() }

    override suspend fun loadData(filters: CatalogFilters): List<AgencyListItemUiModel> = useCases.agencies(
        filters.apiSearch,
        filters.value("country_code"),
        filters.value("type__id")?.toIntOrNull(),
    ).map { it.toListUiModel() }
}

@HiltViewModel
class AgencyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<AgencyDetailUiModel>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    init { retry() }

    override suspend fun loadData(id: String): AgencyDetailUiModel =
        useCases.agency(id.toInt()).toDetailUiModel()
}
