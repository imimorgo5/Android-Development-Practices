package com.example.android_development_practices.ui.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.domain.model.SpaceEvent
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.CatalogOption
import com.example.android_development_practices.ui.common.upcomingYearOptions
import com.example.android_development_practices.ui.common.CatalogFilterDefinition
import com.example.android_development_practices.ui.common.ListScreenScaffold
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.ui.common.formatIsoDate

@Composable
fun EventListRoute(
    onBack: () -> Unit,
    onEventClick: (Int) -> Unit,
    viewModel: EventListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    EventListScreen(
        uiState = uiState,
        onBack = onBack,
        onEventClick = onEventClick,
        onRetry = viewModel::retry,
        onFiltersApplied = viewModel::applyFilters,
    )
}

@Composable
private fun EventListScreen(
    uiState: UiState<List<SpaceEvent>>,
    onBack: () -> Unit,
    onEventClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onFiltersApplied: (com.example.android_development_practices.ui.common.CatalogFilters) -> Unit,
) {
    ListScreenScaffold(
        title = "Ближайшие события",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        onFiltersApplied = onFiltersApplied,
        itemKey = { it.id },
        filterDefinitions = listOf(
            CatalogFilterDefinition("agency__ids", "Агентство") { emptyList() },
            CatalogFilterDefinition("year", "Год") { events ->
                upcomingYearOptions(events.mapNotNull { it.date?.take(4)?.takeIf { year -> year.toIntOrNull() != null } })
            },
            CatalogFilterDefinition("type__ids", "Тип события") { events ->
                events.mapNotNull { event ->
                    val id = event.typeId ?: return@mapNotNull null
                    val name = event.typeName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                    CatalogOption(name, id.toString())
                }
            },
        ),
    ) { event, favorite, toggle ->
        EventListItem(event = event, onClick = { onEventClick(event.id) }, favorite, toggle)
    }
}

@Composable
private fun EventListItem(event: SpaceEvent, onClick: () -> Unit, favorite: Boolean, toggle: () -> Unit) {
    val tag = event.typeName ?: "Событие"
    val date = formatIsoDate(event.date, fallback = "дата уточняется")
    val place = event.location ?: "место не указано"

    ListItemCard(
        thumbnailUrl = event.imageUrl,
        thumbnailFallback = tag,
        title = event.name,
        subtitle = "$date · $place",
        onClick = onClick,
        isFavorite = favorite,
        onFavoriteClick = toggle,
    )
}
