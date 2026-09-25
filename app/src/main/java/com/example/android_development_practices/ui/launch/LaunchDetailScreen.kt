package com.example.android_development_practices.ui.launch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.data.model.dto.Launch
import com.example.android_development_practices.ui.common.DetailHero
import com.example.android_development_practices.ui.common.DetailField
import com.example.android_development_practices.ui.common.InfoChip
import com.example.android_development_practices.ui.common.ScreenScaffold
import com.example.android_development_practices.ui.common.SectionTitle
import com.example.android_development_practices.ui.common.StatBox
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.ui.common.UiStateContent
import com.example.android_development_practices.ui.common.formatIsoDate

@Composable
fun LaunchDetailRoute(
    id: String,
    onBack: () -> Unit,
    viewModel: LaunchDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchDetailScreen(uiState = uiState, onBack = onBack, onRetry = viewModel::retry)
}

@Composable
private fun LaunchDetailScreen(
    uiState: UiState<Launch>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    ScreenScaffold(title = "Детали запуска", onBack = onBack) { contentModifier ->
        UiStateContent(uiState = uiState, onRetry = onRetry, modifier = contentModifier) { launch ->
            LaunchDetailContent(launch = launch)
        }
    }
}

@Composable
private fun LaunchDetailContent(launch: Launch) {
    val rocketFamily = launch.rocket?.configuration?.families?.firstOrNull()?.name ?: "🚀"
    val provider = launch.launch_service_provider?.name ?: "—"
    val missionName = launch.mission?.name ?: "—"
    val orbit = launch.mission?.orbit?.abbrev ?: launch.mission?.orbit?.name ?: "—"
    val window = launch.window_start?.let { formatIsoDate(it) } ?: "—"

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        val hero = createRef()
        val title = createRef()
        val statusChip = createRef()
        val dateText = createRef()
        val statRow = createRef()
        val descTitle = createRef()
        val descText = createRef()
        val fieldsTitle = createRef()
        val fieldsCard = createRef()

        DetailHero(
            emoji = rocketFamily,
            imageUrl = launch.image?.image_url,
            modifier = Modifier.constrainAs(hero) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
            },
        )

        Text(
            text = launch.name,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(hero.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
        )

        val statusText = launch.status?.name ?: launch.status?.abbrev ?: "Статус уточняется"
        InfoChip(
            text = statusText,
            modifier = Modifier.constrainAs(statusChip) {
                top.linkTo(title.bottom, margin = 12.dp)
                centerHorizontallyTo(parent)
            },
        )

        Text(
            text = "Старт: ${formatIsoDate(launch.net, fallback = "дата уточняется")}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.constrainAs(dateText) {
                top.linkTo(statusChip.bottom, margin = 8.dp)
                centerHorizontallyTo(parent)
            },
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .constrainAs(statRow) {
                    top.linkTo(dateText.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
        ) {
            StatBox(
                value = launch.probability?.let { "$it%" } ?: "—",
                label = "Вероятность запуска",
                modifier = Modifier.weight(1f),
            )
            StatBox(
                value = launch.orbital_launch_attempt_count?.toString() ?: "—",
                label = "Запусков ракеты",
                modifier = Modifier.weight(1f),
            )
        }

        SectionTitle(
            text = "О запуске",
            modifier = Modifier.constrainAs(descTitle) {
                top.linkTo(statRow.bottom, margin = 24.dp)
                start.linkTo(parent.start)
            },
        )

        Text(
            text = launch.mission?.description ?: "Описание миссии уточняется.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.constrainAs(descText) {
                top.linkTo(descTitle.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
        )

        SectionTitle(
            text = "Детали",
            modifier = Modifier.constrainAs(fieldsTitle) {
                top.linkTo(descText.bottom, margin = 24.dp)
                start.linkTo(parent.start)
            },
        )

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.constrainAs(fieldsCard) {
                top.linkTo(fieldsTitle.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailField(label = "Организация", value = provider)
                DetailField(label = "Ракета", value = launch.rocket?.configuration?.full_name ?: "—")
                DetailField(label = "Миссия", value = missionName)
                DetailField(label = "Орбита", value = orbit)
                DetailField(
                    label = "Площадка",
                    value = buildList {
                        launch.pad?.name?.let { add(it) }
                        launch.pad?.location?.name?.let { add(it) }
                    }.joinToString(" · ").ifBlank { "—" },
                )
                DetailField(label = "Окно запуска", value = window)
            }
        }
    }
}