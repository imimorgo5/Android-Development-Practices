package com.example.android_development_practices.ui.expedition

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
fun ExpeditionListRoute(
    onBack: () -> Unit,
    onExpeditionClick: (Int) -> Unit,
    viewModel: ExpeditionListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    ExpeditionListScreen(
        uiState = uiState,
        onBack = onBack,
        onExpeditionClick = onExpeditionClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
        initialSettings = settings,
        onOpenFilters = viewModel::loadFilterOptions,
        filterOptions = filterOptions,
        filterDefinitions = viewModel.filterDefinitions,
    )
}

@Composable
private fun ExpeditionListScreen(
    uiState: UiState<List<ExpeditionListItemUiModel>>,
    onBack: () -> Unit,
    onExpeditionClick: (Int) -> Unit,
    onRetry: () -> Unit,
    initialSettings: CatalogSettings?,
    onOpenFilters: () -> Unit,
    filterOptions: Map<String, List<CatalogOption>>,
    filterDefinitions: List<CatalogFilterDefinition>,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Экспедиции",
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
    ) { expedition, favorite, toggle ->
        ExpeditionListItem(expedition = expedition, onClick = { onExpeditionClick(expedition.id) }, favorite, toggle)
    }
}

@Composable
private fun ExpeditionListItem(expedition: ExpeditionListItemUiModel, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    ListItemCard(
        thumbnailUrl = expedition.imageUrl,
        thumbnailFallback = expedition.thumbnailFallback,
        title = expedition.name,
        subtitle = expedition.subtitle,
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
