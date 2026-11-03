package com.example.android_development_practices.domain.model

data class CatalogSettings(
    val filters: CatalogFilters = CatalogFilters(),
    val options: Map<String, List<FilterOption>> = emptyMap(),
)
