package com.example.android_development_practices.ui.expedition

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.domain.SpaceUseCases
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class ExpeditionListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseListViewModel<Expedition>(dispatcher) {

    override suspend fun loadData(): List<Expedition> = useCases.expeditions()
}

@HiltViewModel
class ExpeditionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Expedition>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Expedition =
        useCases.expedition(id.toInt())
}
