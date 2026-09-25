package com.example.android_development_practices.ui.event

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.data.model.dto.SpaceEvent
import com.example.android_development_practices.data.repository.SpaceRepository
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseListViewModel<SpaceEvent>(dispatcher) {

    override suspend fun loadData(): List<SpaceEvent> = repository.getUpcomingEvents()
}

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<SpaceEvent>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): SpaceEvent =
        repository.getEventDetail(id.toInt())
}