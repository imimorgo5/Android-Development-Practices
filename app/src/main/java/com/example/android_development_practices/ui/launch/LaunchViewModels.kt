package com.example.android_development_practices.ui.launch

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.data.model.dto.Launch
import com.example.android_development_practices.data.repository.SpaceRepository
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class LaunchListViewModel @Inject constructor(
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseListViewModel<Launch>(dispatcher) {

    override suspend fun loadData(): List<Launch> = repository.getUpcomingLaunches()
}

@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Launch>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Launch = repository.getLaunchDetail(id)
}