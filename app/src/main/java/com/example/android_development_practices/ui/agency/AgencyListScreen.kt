package com.example.android_development_practices.ui.agency

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
fun AgencyListRoute(
    onBack: () -> Unit,
    onAgencyClick: (Int) -> Unit,
    viewModel: AgencyListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    AgencyListScreen(
        uiState = uiState,
        onBack = onBack,
        onAgencyClick = onAgencyClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
        initialSettings = settings,
        onOpenFilters = viewModel::loadFilterOptions,
        filterOptions = filterOptions,
        filterDefinitions = viewModel.filterDefinitions,
    )
}

@Composable
private fun AgencyListScreen(
    uiState: UiState<List<AgencyListItemUiModel>>,
    onBack: () -> Unit,
    onAgencyClick: (Int) -> Unit,
    onRetry: () -> Unit,
    initialSettings: CatalogSettings?,
    onOpenFilters: () -> Unit,
    filterOptions: Map<String, List<CatalogOption>>,
    filterDefinitions: List<CatalogFilterDefinition>,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Космические агентства",
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
    ) { agency, favorite, toggle ->
        AgencyListItem(agency = agency, onClick = { onAgencyClick(agency.id) }, favorite, toggle)
    }
}

@Composable
private fun AgencyListItem(agency: AgencyListItemUiModel, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    ListItemCard(
        thumbnailUrl = agency.imageUrl,
        thumbnailFallback = agency.thumbnailFallback,
        title = agency.name,
        subtitle = agency.subtitle,
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
