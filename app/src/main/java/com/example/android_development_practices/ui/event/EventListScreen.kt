package com.example.android_development_practices.ui.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.data.model.dto.SpaceEvent
import com.example.android_development_practices.ui.common.ListItemCard
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
    )
}

@Composable
private fun EventListScreen(
    uiState: UiState<List<SpaceEvent>>,
    onBack: () -> Unit,
    onEventClick: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    ListScreenScaffold(
        title = "Ближайшие события",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        itemKey = { it.id },
    ) { event ->
        EventListItem(event = event, onClick = { onEventClick(event.id) })
    }
}

@Composable
private fun EventListItem(event: SpaceEvent, onClick: () -> Unit) {
    val tag = event.type?.name ?: "Событие"
    val date = formatIsoDate(event.date, fallback = "дата уточняется")
    val place = event.location ?: "место не указано"

    ListItemCard(
        thumbnailUrl = event.image?.image_url,
        thumbnailFallback = tag,
        title = event.name,
        subtitle = "$date · $place",
        onClick = onClick,
    )
}