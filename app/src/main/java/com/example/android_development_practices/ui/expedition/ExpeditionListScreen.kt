package com.example.android_development_practices.ui.expedition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.data.model.dto.Expedition
import com.example.android_development_practices.ui.common.ListItemCard
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
    )
}

@Composable
private fun ExpeditionListScreen(
    uiState: UiState<List<Expedition>>,
    onBack: () -> Unit,
    onExpeditionClick: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    ListScreenScaffold(
        title = "Экспедиции",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        itemKey = { it.id },
    ) { expedition ->
        ExpeditionListItem(expedition = expedition, onClick = { onExpeditionClick(expedition.id) })
    }
}

@Composable
private fun ExpeditionListItem(expedition: Expedition, onClick: () -> Unit) {
    val station = expedition.spacestation?.name?.take(3) ?: "МКС"
    val stationName = expedition.spacestation?.name ?: "станция не указана"
    val start = formatIsoDate(expedition.start, fallback = "—")

    ListItemCard(
        thumbnailUrl = expedition.mission_patches.firstOrNull()?.image_url,
        thumbnailFallback = station,
        title = expedition.name,
        subtitle = "$stationName · с $start",
        onClick = onClick,
    )
}