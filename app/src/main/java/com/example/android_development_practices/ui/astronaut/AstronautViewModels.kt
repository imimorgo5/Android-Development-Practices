package com.example.android_development_practices.ui.astronaut

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.domain.SpaceUseCases
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class AstronautListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseListViewModel<Astronaut>(dispatcher) {

    override suspend fun loadData(): List<Astronaut> = useCases.astronauts()
}

@HiltViewModel
class AstronautDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Astronaut>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Astronaut =
        useCases.astronaut(id.toInt())
}
