package com.example.android_development_practices.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun <T : Any> ListScreenScaffold(
    title: String,
    onBack: () -> Unit,
    uiState: UiState<List<T>>,
    onRetry: () -> Unit,
    onFiltersApplied: (CatalogFilters) -> Unit,
    itemKey: (T) -> Any,
    filterDefinitions: List<CatalogFilterDefinition<T>> = emptyList(),
    itemContent: @Composable (T, Boolean, () -> Unit) -> Unit,
) {
    val badgeViewModel: FilterBadgeViewModel = hiltViewModel()
    val badgeActive by badgeViewModel.cache.active.collectAsState()
    val apiOptionsViewModel: ApiCatalogOptionsViewModel = hiltViewModel()
    val apiOptions by apiOptionsViewModel.options.collectAsState()
    val context = LocalContext.current
    val preferences = remember(context) { context.getSharedPreferences("list_filters", 0) }
    val store = remember(preferences, title) { CatalogFilterStore(preferences, title) }
    val initialFilters = remember(title, preferences) { CatalogFilters.load(context, title) }
    var query by remember(title) { mutableStateOf(initialFilters.query) }
    var submittedQuery by remember(title) { mutableStateOf(query) }
    var selectedParams by remember(title) { mutableStateOf(initialFilters.params) }
    var draftParams by remember(title) { mutableStateOf(initialFilters.params) }
    var editing by remember(title) { mutableStateOf(false) }
    var cachedOptions by remember(title, preferences) { mutableStateOf(store.loadOptions()) }

    LaunchedEffect(query) {
        if (query != submittedQuery) {
            delay(400)
            submittedQuery = query
            onFiltersApplied(CatalogFilters(query, selectedParams))
        }
    }
    LaunchedEffect(selectedParams) {
        badgeViewModel.cache.setActive(selectedParams.values.any(String::isNotBlank))
    }

    ScreenScaffold(title = if (editing) "Настройки списка" else title, onBack = {
        if (editing) editing = false else onBack()
    }) { contentModifier ->
        Column(contentModifier.fillMaxSize()) {
            if (!editing || uiState == UiState.Loading) {
                SearchAndFilterRow(
                    query = query,
                    badgeActive = badgeActive,
                    onQueryChange = {
                        query = it
                        store.saveQuery(it)
                    },
                    onSearch = {
                        submittedQuery = query
                        onFiltersApplied(CatalogFilters(query, selectedParams))
                    },
                    onOpenFilters = {
                        apiOptionsViewModel.loadOptions(title.toCatalogKey())
                        draftParams = selectedParams.toMap()
                        editing = true
                    },
                )
            }
            UiStateContent(uiState = uiState, onRetry = onRetry, modifier = Modifier.weight(1f)) { catalogItems ->
                val favoritesViewModel: FavoriteViewModel = hiltViewModel()
                val favorites by favoritesViewModel.favorites.collectAsState()
                val favoriteKeys = remember(favorites) { favorites.mapTo(mutableSetOf()) { it.key } }
                val currentOptions = filterDefinitions.associate { definition ->
                    val fromApiOptions = apiOptions[definition.key].orEmpty()
                    val itemsOptions = definition.options(catalogItems)
                    definition.key to (fromApiOptions.ifEmpty { itemsOptions })
                        .distinctBy(CatalogOption::value).sortedBy(CatalogOption::label)
                }
                LaunchedEffect(currentOptions, query, selectedParams) {
                    if (query.isBlank() && selectedParams.values.none(String::isNotBlank)) {
                        cachedOptions = currentOptions
                        store.saveOptions(currentOptions)
                    }
                }

                if (editing) {
                    FilterSettingsContent(
                        definitions = filterDefinitions,
                        options = currentOptions.mapValues { (key, value) ->
                            apiOptions[key].orEmpty().ifEmpty { cachedOptions[key].orEmpty().ifEmpty { value } }
                        },
                        values = draftParams,
                        onValueChange = { key, value -> draftParams = draftParams.toMutableMap().apply {
                            if (value.isBlank()) remove(key) else put(key, value)
                        } },
                        onReset = {
                            draftParams = emptyMap()
                            selectedParams = emptyMap()
                            store.saveFilters(emptyMap())
                            editing = false
                            submittedQuery = query
                            onFiltersApplied(CatalogFilters(query))
                        },
                        onApply = {
                            selectedParams = draftParams.filterValues(String::isNotBlank)
                            store.saveFilters(selectedParams)
                            editing = false
                            submittedQuery = query
                            onFiltersApplied(CatalogFilters(query, selectedParams))
                        },
                    )
                } else if (catalogItems.isEmpty()) {
                    Text("Ничего не найдено", Modifier.fillMaxWidth().padding(28.dp), textAlign = TextAlign.Center)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(items = catalogItems, key = itemKey) { item ->
                            val favorite = favoriteSnapshot(item)
                            itemContent(item, favorite.key in favoriteKeys) { favoritesViewModel.toggle(favorite) }
                        }
                    }
                }
            }
        }
    }
}

private fun String.toCatalogKey(): String = when (this) {
    "Ближайшие запуски" -> "launch"
    "Ближайшие события" -> "event"
    "Экспедиции" -> "expedition"
    "Космические агентства" -> "agency"
    "Космонавты и астронавты" -> "astronaut"
    else -> ""
}
