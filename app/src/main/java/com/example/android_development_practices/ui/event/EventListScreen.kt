package com.example.android_development_practices.ui.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.CatalogFilterDefinition
import com.example.android_development_practices.ui.common.ListScreenScaffold
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.domain.model.CatalogSettings

@Composable
fun EventListRoute(
    onBack: () -> Unit,
    onEventClick: (Int) -> Unit,
    viewModel: EventListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    EventListScreen(
        uiState = uiState,
        onBack = onBack,
        onEventClick = onEventClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
        initialSettings = settings,
        onOpenFilters = viewModel::loadFilterOptions,
        filterOptions = filterOptions,
        filterDefinitions = viewModel.filterDefinitions,
    )
}

@Composable
private fun EventListScreen(
    uiState: UiState<List<EventListItemUiModel>>,
    onBack: () -> Unit,
    onEventClick: (Int) -> Unit,
    onRetry: () -> Unit,
    initialSettings: CatalogSettings?,
    onOpenFilters: () -> Unit,
    filterOptions: Map<String, List<CatalogOption>>,
    filterDefinitions: List<CatalogFilterDefinition>,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Ближайшие события",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        onFiltersApplied = onFiltersApplied,
        initialSettings = initialSettings,
        onOpenFilters = onOpenFilters,
        favoriteItem = { it.source },
        itemKey = { it.id },
        filterDefinitions = filterDefinitions,
        filterOptions = filterOptions,
    ) { event, favorite, toggle ->
        EventListItem(event = event, onClick = { onEventClick(event.id) }, favorite, toggle)
    }
}

@Composable
private fun EventListItem(event: EventListItemUiModel, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    ListItemCard(
        thumbnailUrl = event.imageUrl,
        thumbnailFallback = event.thumbnailFallback,
        title = event.name,
        subtitle = event.subtitle,
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
