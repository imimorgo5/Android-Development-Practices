package com.example.android_development_practices.ui.agency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.ListScreenScaffold
import com.example.android_development_practices.ui.common.CatalogFilterDefinition
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.UiState

@Composable
fun AgencyListRoute(
    onBack: () -> Unit,
    onAgencyClick: (Int) -> Unit,
    viewModel: AgencyListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    AgencyListScreen(
        uiState = uiState,
        onBack = onBack,
        onAgencyClick = onAgencyClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
    )
}

@Composable
private fun AgencyListScreen(
    uiState: UiState<List<Agency>>,
    onBack: () -> Unit,
    onAgencyClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Космические агентства",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        onFiltersApplied = onFiltersApplied,
        itemKey = { it.id },
        filterDefinitions = listOf(
            CatalogFilterDefinition("country_code", "Страна") { agencies ->
                agencies.flatMap { agency -> agency.countryFilterValues.map { CatalogOption(it.name, it.alpha3Code) } }
            },
            CatalogFilterDefinition("type__id", "Тип агентства") { agencies ->
                agencies.mapNotNull { agency ->
                    val id = agency.typeId ?: return@mapNotNull null
                    val name = agency.typeName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    CatalogOption(name, id.toString())
                }
            },
        ),
    ) { agency, favorite, toggle ->
        AgencyListItem(agency = agency, onClick = { onAgencyClick(agency.id) }, favorite, toggle)
    }
}

@Composable
private fun AgencyListItem(agency: Agency, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    val abbrev = agency.abbreviation ?: agency.name.take(3)
    val country = agency.countries.joinToString().ifBlank { "—" }
    val type = agency.typeName ?: "агентство"

    ListItemCard(
        thumbnailUrl = agency.imageUrl,
        thumbnailFallback = abbrev,
        title = agency.name,
        subtitle = "$country · $type",
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
