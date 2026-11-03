package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.local.CatalogSettingsStore
import com.example.android_development_practices.domain.model.CatalogFilters
import com.example.android_development_practices.domain.model.FilterOption
import com.example.android_development_practices.domain.repository.CatalogSettingsRepository
import javax.inject.Inject

class CatalogSettingsRepositoryImpl @Inject constructor(private val store: CatalogSettingsStore) : CatalogSettingsRepository {
    override fun load(catalog: String) = store.load(catalog)
    override fun saveFilters(catalog: String, filters: CatalogFilters) = store.saveFilters(catalog, filters)
    override fun saveOptions(catalog: String, options: Map<String, List<FilterOption>>) = store.saveOptions(catalog, options)
}
