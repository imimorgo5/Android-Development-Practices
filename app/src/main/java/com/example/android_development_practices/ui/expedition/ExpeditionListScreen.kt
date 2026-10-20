package com.example.android_development_practices.ui.expedition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.CatalogFilterDefinition
import com.example.android_development_practices.ui.common.ListScreenScaffold
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.ui.common.formatIsoDate

@Composable
fun ExpeditionListRoute(
    onBack: () -> Unit,
    onExpeditionClick: (Int) -> Unit,
    viewModel: ExpeditionListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    ExpeditionListScreen(
        uiState = uiState,
        onBack = onBack,
        onExpeditionClick = onExpeditionClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
    )
}

@Composable
private fun ExpeditionListScreen(
    uiState: UiState<List<Expedition>>,
    onBack: () -> Unit,
    onExpeditionClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Экспедиции",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        onFiltersApplied = onFiltersApplied,
        itemKey = { it.id },
        filterDefinitions = listOf(
            CatalogFilterDefinition("crew__astronaut__agency", "Агентство") { emptyList() },
            CatalogFilterDefinition("start__gte", "Дата начала от", isDate = true) { expeditions ->
                expeditions.mapNotNull { it.start?.take(10)?.let { date -> CatalogOption(date, date) } }
            },
            CatalogFilterDefinition("end__lte", "Дата окончания до", isDate = true) { expeditions ->
                expeditions.mapNotNull { it.end?.take(10)?.let { date -> CatalogOption(date, date) } }
            },
            CatalogFilterDefinition("space_station", "Космическая станция") { expeditions ->
                expeditions.mapNotNull { expedition ->
                    val id = expedition.stationId ?: return@mapNotNull null
                    val name = expedition.stationName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    CatalogOption(name, id.toString())
                }
            },
        ),
    ) { expedition, favorite, toggle ->
        ExpeditionListItem(expedition = expedition, onClick = { onExpeditionClick(expedition.id) }, favorite, toggle)
    }
}

@Composable
private fun ExpeditionListItem(expedition: Expedition, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    val station = expedition.stationName?.take(3) ?: "МКС"
    val stationName = expedition.stationName ?: "станция не указана"
    val start = formatIsoDate(expedition.start, fallback = "—")

    ListItemCard(
        thumbnailUrl = expedition.patchImageUrl,
        thumbnailFallback = station,
        title = expedition.name,
        subtitle = "$stationName · с $start",
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
