package com.example.android_development_practices.ui.agency

import androidx.lifecycle.SavedStateHandle
import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.domain.SpaceUseCases
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.ui.common.BaseDetailViewModel
import com.example.android_development_practices.ui.common.BaseListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class AgencyListViewModel @Inject constructor(
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseListViewModel<Agency>(dispatcher) {

    override suspend fun loadData(): List<Agency> = useCases.agencies()
}

@HiltViewModel
class AgencyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: SpaceUseCases,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseDetailViewModel<Agency>(
    argId = checkNotNull(savedStateHandle["id"]),
    ioDispatcher = dispatcher,
) {
    override suspend fun loadData(id: String): Agency =
        useCases.agency(id.toInt())
}
