package com.example.android_development_practices.ui.astronaut

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.ListScreenScaffold
import com.example.android_development_practices.ui.common.CatalogFilterDefinition
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.domain.model.CatalogSettings

@Composable
fun AstronautListRoute(
    onBack: () -> Unit,
    onAstronautClick: (Int) -> Unit,
    viewModel: AstronautListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    AstronautListScreen(
        uiState = uiState,
        onBack = onBack,
        onAstronautClick = onAstronautClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
        initialSettings = settings,
        onOpenFilters = viewModel::loadFilterOptions,
        filterOptions = filterOptions,
        filterDefinitions = viewModel.filterDefinitions,
    )
}

@Composable
private fun AstronautListScreen(
    uiState: UiState<List<AstronautListItemUiModel>>,
    onBack: () -> Unit,
    onAstronautClick: (Int) -> Unit,
    onRetry: () -> Unit,
    initialSettings: CatalogSettings?,
    onOpenFilters: () -> Unit,
    filterOptions: Map<String, List<CatalogOption>>,
    filterDefinitions: List<CatalogFilterDefinition>,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Космонавты и астронавты",
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
    ) { astronaut, favorite, toggle ->
        AstronautListItem(astronaut = astronaut, onClick = { onAstronautClick(astronaut.id) }, favorite, toggle)
    }
}

@Composable
private fun AstronautListItem(astronaut: AstronautListItemUiModel, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    ListItemCard(
        thumbnailUrl = astronaut.imageUrl,
        thumbnailFallback = astronaut.thumbnailFallback,
        title = astronaut.name,
        subtitle = astronaut.subtitle,
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
