package com.example.android_development_practices.ui.common

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

data class CatalogFilters(
    val query: String = "",
    val params: Map<String, String> = emptyMap(),
) {
    val apiSearch: String?
        get() = query.trim().takeIf(String::isNotEmpty)

    fun value(key: String): String? = params[key]?.takeIf(String::isNotBlank)

    companion object {
        fun load(context: Context, title: String): CatalogFilters {
            val preferences = context.getSharedPreferences("list_filters", Context.MODE_PRIVATE)
            val params = preferences.all
                .filterKeys { it.startsWith("$title.filter.") }
                .mapKeys { it.key.removePrefix("$title.filter.") }
                .mapValues { it.value as? String ?: "" }
                .filterValues(String::isNotBlank)
            return CatalogFilters(preferences.getString("$title.query", "").orEmpty(), params)
        }
    }
}

/** Persists one catalog's search, selected filters, and option labels. */
internal class CatalogFilterStore(private val preferences: SharedPreferences, private val title: String) {
    private fun filterKey(key: String) = "$title.filter.$key"

    fun saveQuery(query: String) {
        preferences.edit().putString("$title.query", query).apply()
    }

    fun saveFilters(filters: Map<String, String>) {
        preferences.edit().apply {
            preferences.all.keys.filter { it.startsWith("$title.filter.") && it != "$title.filter_options" }
                .forEach(::remove)
            filters.forEach { (key, value) -> if (value.isNotBlank()) putString(filterKey(key), value) }
        }.apply()
    }

    fun saveOptions(options: Map<String, List<CatalogOption>>) {
        preferences.edit().putString("$title.filter_options", saveCachedOptions(options)).apply()
    }

    fun loadOptions(): Map<String, List<CatalogOption>> =
        loadCachedOptions(preferences.getString("$title.filter_options", null))
}

data class CatalogOption(val label: String, val value: String)

data class CatalogFilterDefinition<T>(
    val key: String,
    val label: String,
    val isDate: Boolean = false,
    val options: (List<T>) -> List<CatalogOption>,
)

fun upcomingYearOptions(observed: Iterable<String>): List<CatalogOption> =
    (observed + (Calendar.getInstance().get(Calendar.YEAR)..Calendar.getInstance().get(Calendar.YEAR) + 5).map(Int::toString))
        .distinct().sorted().map { CatalogOption(it, it) }

private fun loadCachedOptions(serialized: String?): Map<String, List<CatalogOption>> = runCatching {
    val root = JSONObject(serialized ?: return emptyMap())
    root.keys().asSequence().associateWith { key ->
        val array = root.optJSONArray(key) ?: JSONArray()
        (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let { CatalogOption(it.optString("label"), it.optString("value")) }
        }
    }
}.getOrDefault(emptyMap())

private fun saveCachedOptions(options: Map<String, List<CatalogOption>>): String {
    val root = JSONObject()
    options.forEach { (key, values) ->
        root.put(key, JSONArray().apply {
            values.forEach { option -> put(JSONObject().put("label", option.label).put("value", option.value)) }
        })
    }
    return root.toString()
}
