package com.example.android_development_practices.data.local

import android.content.Context
import com.example.android_development_practices.domain.model.CatalogFilters
import com.example.android_development_practices.domain.model.CatalogSettings
import com.example.android_development_practices.domain.model.FilterOption
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import org.json.JSONArray
import org.json.JSONObject

@Singleton
class CatalogSettingsStore @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val preferences by lazy { context.getSharedPreferences("list_filters", Context.MODE_PRIVATE) }

    fun load(catalog: String): CatalogSettings {
        val params = preferences.all
            .filterKeys { it.startsWith("$catalog.filter.") }
            .mapKeys { it.key.removePrefix("$catalog.filter.") }
            .mapValues { it.value as? String ?: "" }
            .filterValues(String::isNotBlank)
        return CatalogSettings(
            filters = CatalogFilters(preferences.getString("$catalog.query", "").orEmpty(), params),
            options = loadCachedOptions(preferences.getString("$catalog.filter_options", null)),
        )
    }

    fun saveFilters(catalog: String, filters: CatalogFilters) {
        preferences.edit().apply {
            putString("$catalog.query", filters.query)
            preferences.all.keys.filter { it.startsWith("$catalog.filter.") }.forEach(::remove)
            filters.params.forEach { (key, value) ->
                if (value.isNotBlank()) putString("$catalog.filter.$key", value)
            }
        }.apply()
    }

    fun saveOptions(catalog: String, options: Map<String, List<FilterOption>>) {
        preferences.edit().putString("$catalog.filter_options", saveCachedOptions(options)).apply()
    }
}

private fun loadCachedOptions(serialized: String?): Map<String, List<FilterOption>> = runCatching {
    val root = JSONObject(serialized ?: return emptyMap())
    root.keys().asSequence().associateWith { key ->
        val array = root.optJSONArray(key) ?: JSONArray()
        (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let { FilterOption(it.optString("label"), it.optString("value")) }
        }
    }
}.getOrDefault(emptyMap())

private fun saveCachedOptions(options: Map<String, List<FilterOption>>): String {
    val root = JSONObject()
    options.forEach { (key, values) ->
        root.put(key, JSONArray().apply {
            values.forEach { option -> put(JSONObject().put("label", option.label).put("value", option.value)) }
        })
    }
    return root.toString()
}
