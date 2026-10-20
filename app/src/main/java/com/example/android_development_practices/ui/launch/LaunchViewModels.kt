package com.example.android_development_practices.ui.launch

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.domain.SpaceUseCases
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class LaunchListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseListViewModel<Launch>(dispatcher) {

    override suspend fun loadData(): List<Launch> = useCases.upcomingLaunches()
}

@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Launch>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Launch = useCases.launch(id)
}
