package com.example.android_development_practices.ui.astronaut

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.ListScreenScaffold
import com.example.android_development_practices.ui.common.CatalogFilterDefinition
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.UiState

@Composable
fun AstronautListRoute(
    onBack: () -> Unit,
    onAstronautClick: (Int) -> Unit,
    viewModel: AstronautListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    AstronautListScreen(
        uiState = uiState,
        onBack = onBack,
        onAstronautClick = onAstronautClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
    )
}

@Composable
private fun AstronautListScreen(
    uiState: UiState<List<Astronaut>>,
    onBack: () -> Unit,
    onAstronautClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Космонавты и астронавты",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        onFiltersApplied = onFiltersApplied,
        itemKey = { it.id },
        filterDefinitions = listOf(
            CatalogFilterDefinition("nationality", "Национальность") { astronauts ->
                astronauts.flatMap { astronaut ->
                    astronaut.nationalityFilterValues.map { CatalogOption(it, it) }
                }
            },
            CatalogFilterDefinition("agency_ids", "Агентство") { astronauts ->
                astronauts.mapNotNull { astronaut ->
                    val id = astronaut.agencyId ?: return@mapNotNull null
                    val name = astronaut.agencyName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    CatalogOption(name, id.toString())
                }
            },
            CatalogFilterDefinition("status_ids", "Статус") { astronauts ->
                astronauts.mapNotNull { astronaut ->
                    val id = astronaut.statusId ?: return@mapNotNull null
                    val name = astronaut.statusName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    CatalogOption(name, id.toString())
                }
            },
        ),
    ) { astronaut, favorite, toggle ->
        AstronautListItem(astronaut = astronaut, onClick = { onAstronautClick(astronaut.id) }, favorite, toggle)
    }
}

@Composable
private fun AstronautListItem(astronaut: Astronaut, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    val initials = astronaut.name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .takeLast(2)
        .joinToString("")
        .ifBlank { "🧑‍🚀" }
    val agency = astronaut.agencyAbbreviation ?: ""
    val status = astronaut.statusName ?: ""
    val subtitle = listOf(agency, status).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { "—" }

    ListItemCard(
        thumbnailUrl = astronaut.imageUrl,
        thumbnailFallback = initials,
        title = astronaut.name,
        subtitle = subtitle,
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
