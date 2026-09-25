package com.example.android_development_practices.ui.expedition

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.data.model.dto.Expedition
import com.example.android_development_practices.data.repository.SpaceRepository
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class ExpeditionListViewModel @Inject constructor(
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseListViewModel<Expedition>(dispatcher) {

    override suspend fun loadData(): List<Expedition> = repository.getExpeditions()
}

@HiltViewModel
class ExpeditionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Expedition>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Expedition =
        repository.getExpeditionDetail(id.toInt())
}