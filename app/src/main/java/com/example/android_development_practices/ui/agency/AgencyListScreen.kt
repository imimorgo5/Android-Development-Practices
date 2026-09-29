package com.example.android_development_practices.ui.agency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.data.model.dto.Agency
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.ListScreenScaffold
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
    )
}

@Composable
private fun AgencyListScreen(
    uiState: UiState<List<Agency>>,
    onBack: () -> Unit,
    onAgencyClick: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    ListScreenScaffold(
        title = "Космические агентства",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        itemKey = { it.id },
    ) { agency ->
        AgencyListItem(agency = agency, onClick = { onAgencyClick(agency.id) })
    }
}

@Composable
private fun AgencyListItem(agency: Agency, onClick: () -> Unit) {
    val abbrev = agency.abbrev ?: agency.name.take(3)
    val country = agency.country.firstOrNull()?.name ?: "—"
    val type = agency.type?.name ?: "агентство"

    ListItemCard(
        thumbnailUrl = agency.image?.image_url,
        thumbnailFallback = abbrev,
        title = agency.name,
        subtitle = "$country · $type",
        onClick = onClick,
    )
}