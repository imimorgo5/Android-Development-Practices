package com.example.android_development_practices.ui.agency

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.data.model.dto.Agency
import com.example.android_development_practices.data.repository.SpaceRepository
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class AgencyListViewModel @Inject constructor(
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseListViewModel<Agency>(dispatcher) {

    override suspend fun loadData(): List<Agency> = repository.getAgencies()
}

@HiltViewModel
class AgencyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SpaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Agency>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Agency =
        repository.getAgencyDetail(id.toInt())
}