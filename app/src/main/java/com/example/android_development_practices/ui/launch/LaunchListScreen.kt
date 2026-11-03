package com.example.android_development_practices.ui.launch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.ListScreenScaffold
import com.example.android_development_practices.ui.common.CatalogFilterDefinition
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.domain.model.CatalogSettings

@Composable
fun LaunchListRoute(
    onBack: () -> Unit,
    onLaunchClick: (String) -> Unit,
    viewModel: LaunchListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    LaunchListScreen(
        uiState = uiState,
        onBack = onBack,
        onLaunchClick = onLaunchClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
        initialSettings = settings,
        onOpenFilters = viewModel::loadFilterOptions,
        filterOptions = filterOptions,
        filterDefinitions = viewModel.filterDefinitions,
    )
}

@Composable
private fun LaunchListScreen(
    uiState: UiState<List<LaunchListItemUiModel>>,
    onBack: () -> Unit,
    onLaunchClick: (String) -> Unit,
    onRetry: () -> Unit,
    initialSettings: CatalogSettings?,
    onOpenFilters: () -> Unit,
    filterOptions: Map<String, List<CatalogOption>>,
    filterDefinitions: List<CatalogFilterDefinition>,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Ближайшие запуски",
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
    ) { launch, favorite, toggle ->
        LaunchListItem(launch = launch, onClick = { onLaunchClick(launch.id) }, favorite, toggle)
    }
}

@Composable
private fun LaunchListItem(launch: LaunchListItemUiModel, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    ListItemCard(
        thumbnailUrl = launch.imageUrl,
        thumbnailFallback = launch.thumbnailFallback,
        title = launch.name,
        subtitle = launch.subtitle,
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
