package com.example.android_development_practices.ui.agency

import androidx.lifecycle.SavedStateHandle
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.android_development_practices.domain.model.Agency
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
class AgencyListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    @ApplicationContext context: Context,
) : BaseListViewModel<Agency>(dispatcher, CatalogFilters.load(context, "Космические агентства")) {

    override suspend fun loadData(filters: CatalogFilters): List<Agency> = useCases.agencies(
        filters.apiSearch,
        filters.value("country_code"),
        filters.value("type__id")?.toIntOrNull(),
    )
}

@HiltViewModel
class AgencyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    private val detailsCache: FavoriteDetailsCache,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Agency>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Agency =
        detailsCache.load("agency", id, Agency.serializer()) { useCases.agency(id.toInt()) }
}
