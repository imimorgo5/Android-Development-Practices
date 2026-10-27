package com.example.android_development_practices.ui.launch

import androidx.lifecycle.SavedStateHandle
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.android_development_practices.domain.model.Launch
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
class LaunchListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    @ApplicationContext context: Context,
) : BaseListViewModel<Launch>(dispatcher, CatalogFilters.load(context, "Ближайшие запуски")) {

    override suspend fun loadData(filters: CatalogFilters): List<Launch> = useCases.upcomingLaunches(
        filters.apiSearch,
        filters.value("lsp__id")?.toIntOrNull(),
        filters.value("year")?.toIntOrNull(),
        filters.value("status__ids")?.toIntOrNull(),
    )
}

@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    private val detailsCache: FavoriteDetailsCache,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Launch>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Launch = detailsCache.load("launch", id, Launch.serializer()) { useCases.launch(id) }
}
