package com.example.android_development_practices.domain

import com.example.android_development_practices.domain.model.CatalogFilters
import com.example.android_development_practices.domain.model.FilterOption
import com.example.android_development_practices.domain.repository.CatalogSettingsRepository
import javax.inject.Inject

class CatalogSettingsUseCases @Inject constructor(private val repository: CatalogSettingsRepository) {
    suspend fun load(catalog: String) = repository.load(catalog)
    suspend fun saveFilters(catalog: String, filters: CatalogFilters) = repository.saveFilters(catalog, filters)
    suspend fun saveOptions(catalog: String, options: Map<String, List<FilterOption>>) = repository.saveOptions(catalog, options)
}
