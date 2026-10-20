package com.example.android_development_practices.ui.astronaut

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.ListScreenScaffold
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
    )
}

@Composable
private fun AstronautListScreen(
    uiState: UiState<List<Astronaut>>,
    onBack: () -> Unit,
    onAstronautClick: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    ListScreenScaffold(
        title = "Космонавты и астронавты",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        itemKey = { it.id },
    ) { astronaut ->
        AstronautListItem(astronaut = astronaut, onClick = { onAstronautClick(astronaut.id) })
    }
}

@Composable
private fun AstronautListItem(astronaut: Astronaut, onClick: () -> Unit) {
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
    )
}
