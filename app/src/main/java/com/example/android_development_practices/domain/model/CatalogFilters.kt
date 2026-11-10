package com.example.android_development_practices.domain.model

data class CatalogFilters(
    val query: String = "",
    val params: Map<String, String> = emptyMap(),
) {
    val apiSearch: String?
        get() = query.trim().takeIf(String::isNotEmpty)

    fun value(key: String): String? = params[key]?.takeIf(String::isNotBlank)
}
