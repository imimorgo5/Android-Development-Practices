package com.example.android_development_practices.ui.launch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.ListScreenScaffold
import com.example.android_development_practices.ui.common.CatalogFilterDefinition
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.upcomingYearOptions
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
    LaunchListScreen(
        uiState = uiState,
        onBack = onBack,
        onLaunchClick = onLaunchClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
        initialSettings = settings,
        onFilterOptionsChanged = viewModel::saveFilterOptions,
    )
}

@Composable
private fun LaunchListScreen(
    uiState: UiState<List<LaunchListItemUiModel>>,
    onBack: () -> Unit,
    onLaunchClick: (String) -> Unit,
    onRetry: () -> Unit,
    initialSettings: CatalogSettings,
    onFilterOptionsChanged: (Map<String, List<CatalogOption>>) -> Unit,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Ближайшие запуски",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        onFiltersApplied = onFiltersApplied,
        initialSettings = initialSettings,
        onFilterOptionsChanged = onFilterOptionsChanged,
        favoriteItem = { it.source },
        itemKey = { it.id },
        filterDefinitions = listOf(
            CatalogFilterDefinition("lsp__id", "Агентство") { launches ->
                launches.mapNotNull { launch ->
                    val id = launch.source.providerId ?: return@mapNotNull null
                    val name = launch.source.providerName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    CatalogOption(name, id.toString())
                }
            },
            CatalogFilterDefinition("year", "Год") { launches ->
                upcomingYearOptions(launches.mapNotNull { it.source.net?.take(4)?.takeIf { year -> year.toIntOrNull() != null } })
            },
            CatalogFilterDefinition("status__ids", "Статус") { launches ->
                launches.mapNotNull { launch ->
                    val id = launch.source.statusId ?: return@mapNotNull null
                    val name = launch.source.status?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    CatalogOption(name, id.toString())
                }
            },
        ),
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
