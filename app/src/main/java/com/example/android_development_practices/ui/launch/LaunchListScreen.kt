package com.example.android_development_practices.ui.launch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.ListScreenScaffold
import com.example.android_development_practices.ui.common.CatalogFilterDefinition
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.upcomingYearOptions
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.ui.common.formatIsoDate

@Composable
fun LaunchListRoute(
    onBack: () -> Unit,
    onLaunchClick: (String) -> Unit,
    viewModel: LaunchListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchListScreen(
        uiState = uiState,
        onBack = onBack,
        onLaunchClick = onLaunchClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
    )
}

@Composable
private fun LaunchListScreen(
    uiState: UiState<List<Launch>>,
    onBack: () -> Unit,
    onLaunchClick: (String) -> Unit,
    onRetry: () -> Unit,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Ближайшие запуски",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        onFiltersApplied = onFiltersApplied,
        itemKey = { it.id },
        filterDefinitions = listOf(
            CatalogFilterDefinition("lsp__id", "Агентство") { launches ->
                launches.mapNotNull { launch ->
                    val id = launch.providerId ?: return@mapNotNull null
                    val name = launch.providerName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    CatalogOption(name, id.toString())
                }
            },
            CatalogFilterDefinition("year", "Год") { launches ->
                upcomingYearOptions(launches.mapNotNull { it.net?.take(4)?.takeIf { year -> year.toIntOrNull() != null } })
            },
            CatalogFilterDefinition("status__ids", "Статус") { launches ->
                launches.mapNotNull { launch ->
                    val id = launch.statusId ?: return@mapNotNull null
                    val name = launch.status?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    CatalogOption(name, id.toString())
                }
            },
        ),
    ) { launch, favorite, toggle ->
        LaunchListItem(launch = launch, onClick = { onLaunchClick(launch.id) }, favorite, toggle)
    }
}

@Composable
private fun LaunchListItem(launch: Launch, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    val rocketCode = launch.rocketFamily?.take(3)
        ?: launch.providerName?.take(3)
        ?: "🚀"
    val provider = launch.providerName ?: "—"
    val date = formatIsoDate(launch.net, fallback = "дата уточняется")

    ListItemCard(
        thumbnailUrl = launch.imageUrl,
        thumbnailFallback = rocketCode,
        title = launch.name,
        subtitle = "$provider · $date",
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
