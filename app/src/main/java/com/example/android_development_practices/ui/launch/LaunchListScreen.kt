package com.example.android_development_practices.ui.launch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.ui.common.ListItemCard
import com.example.android_development_practices.ui.common.ListScreenScaffold
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
    )
}

@Composable
private fun LaunchListScreen(
    uiState: UiState<List<Launch>>,
    onBack: () -> Unit,
    onLaunchClick: (String) -> Unit,
    onRetry: () -> Unit,
) {
    ListScreenScaffold(
        title = "Ближайшие запуски",
        onBack = onBack,
        uiState = uiState,
        onRetry = onRetry,
        itemKey = { it.id },
    ) { launch ->
        LaunchListItem(launch = launch, onClick = { onLaunchClick(launch.id) })
    }
}

@Composable
private fun LaunchListItem(launch: Launch, onClick: () -> Unit) {
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
    )
}
