package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.local.CatalogSettingsStore
import com.example.android_development_practices.di.IoDispatcher
import com.example.android_development_practices.domain.model.CatalogFilters
import com.example.android_development_practices.domain.model.FilterOption
import com.example.android_development_practices.domain.repository.CatalogSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Singleton
class CatalogSettingsRepositoryImpl @Inject constructor(
    private val store: CatalogSettingsStore,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
) : CatalogSettingsRepository {
    private val mutex = Mutex()

    override suspend fun load(catalog: String) = withContext(dispatcher) {
        mutex.withLock { store.load(catalog) }
    }
    override suspend fun saveFilters(catalog: String, filters: CatalogFilters) = withContext(dispatcher) {
        mutex.withLock { store.saveFilters(catalog, filters) }
    }
    override suspend fun saveOptions(catalog: String, options: Map<String, List<FilterOption>>) = withContext(dispatcher) {
        mutex.withLock { store.saveOptions(catalog, options) }
    }
}
