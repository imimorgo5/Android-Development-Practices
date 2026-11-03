package com.example.android_development_practices.ui.launch

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
) : BaseListViewModel<LaunchListItemUiModel>(dispatcher, settingsUseCases, "Ближайшие запуски") {

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
    override suspend fun loadData(id: String): LaunchDetailUiModel = useCases.launch(id).toDetailUiModel()
}
