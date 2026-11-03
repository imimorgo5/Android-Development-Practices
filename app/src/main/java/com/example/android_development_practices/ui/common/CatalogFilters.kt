package com.example.android_development_practices.ui.common

import java.util.Calendar

typealias CatalogFilters = com.example.android_development_practices.domain.model.CatalogFilters
typealias CatalogOption = com.example.android_development_practices.domain.model.FilterOption

data class CatalogFilterDefinition<T>(
    val key: String,
    val label: String,
    val isDate: Boolean = false,
    val options: (List<T>) -> List<CatalogOption>,
)

fun upcomingYearOptions(observed: Iterable<String>): List<CatalogOption> =
    (observed + (Calendar.getInstance().get(Calendar.YEAR)..Calendar.getInstance().get(Calendar.YEAR) + 5).map(Int::toString))
        .distinct().sorted().map { CatalogOption(it, it) }
