package com.example.android_development_practices.domain.repository

import com.example.android_development_practices.domain.model.CatalogFilters
import com.example.android_development_practices.domain.model.CatalogSettings
import com.example.android_development_practices.domain.model.FilterOption

interface CatalogSettingsRepository {
    fun load(catalog: String): CatalogSettings
    fun saveFilters(catalog: String, filters: CatalogFilters)
    fun saveOptions(catalog: String, options: Map<String, List<FilterOption>>)
}
