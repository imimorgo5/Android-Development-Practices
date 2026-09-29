package com.example.android_development_practices.ui.launch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.data.model.dto.Launch
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
    val rocketCode = launch.rocket?.configuration?.families?.firstOrNull()?.name?.take(3)
        ?: launch.launch_service_provider?.abbrev
        ?: "🚀"
    val provider = launch.launch_service_provider?.name ?: "—"
    val date = formatIsoDate(launch.net, fallback = "дата уточняется")

    ListItemCard(
        thumbnailUrl = launch.image?.image_url,
        thumbnailFallback = rocketCode,
        title = launch.name,
        subtitle = "$provider · $date",
        onClick = onClick,
    )
}