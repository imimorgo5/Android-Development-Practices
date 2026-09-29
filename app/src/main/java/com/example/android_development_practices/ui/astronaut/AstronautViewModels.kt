package com.example.android_development_practices.ui.astronaut

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.data.model.dto.Astronaut
import com.example.android_development_practices.data.repository.SpaceRepository
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class AstronautListViewModel @Inject constructor(
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseListViewModel<Astronaut>(dispatcher) {

    override suspend fun loadData(): List<Astronaut> = repository.getAstronauts()
}

@HiltViewModel
class AstronautDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Astronaut>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Astronaut =
        repository.getAstronautDetail(id.toInt())
}