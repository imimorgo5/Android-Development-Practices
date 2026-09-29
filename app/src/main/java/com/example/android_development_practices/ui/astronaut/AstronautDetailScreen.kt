package com.example.android_development_practices.ui.astronaut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.android_development_practices.data.model.dto.Astronaut
import com.example.android_development_practices.ui.common.DetailField
import com.example.android_development_practices.ui.common.DetailHero
import com.example.android_development_practices.ui.common.InfoChip
import com.example.android_development_practices.ui.common.ScreenScaffold
import com.example.android_development_practices.ui.common.SectionTitle
import com.example.android_development_practices.ui.common.StatBox
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.ui.common.UiStateContent
import com.example.android_development_practices.ui.common.formatIsoDate
import com.example.android_development_practices.ui.common.formatIsoDuration

@Composable
fun AstronautDetailRoute(
    id: String,
    onBack: () -> Unit,
    viewModel: AstronautDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    AstronautDetailScreen(uiState = uiState, onBack = onBack, onRetry = viewModel::retry)
}

@Composable
private fun AstronautDetailScreen(
    uiState: UiState<Astronaut>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    ScreenScaffold(title = "О космонавте/Об астронавте", onBack = onBack) { contentModifier ->
        UiStateContent(uiState = uiState, onRetry = onRetry, modifier = contentModifier) { astronaut ->
            AstronautDetailContent(astronaut = astronaut)
        }
    }
}

@Composable
private fun AstronautDetailContent(astronaut: Astronaut) {
    val initials = astronaut.name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .takeLast(2)
        .joinToString("")
        .ifBlank { "🧑‍🚀" }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        val hero = createRef()
        val title = createRef()
        val agencyChip = createRef()
        val statusText = createRef()
        val statRow = createRef()
        val bioTitle = createRef()
        val bioText = createRef()
        val fieldsTitle = createRef()
        val fieldsCard = createRef()

        DetailHero(
            emoji = initials,
            imageUrl = astronaut.image?.image_url,
            modifier = Modifier.constrainAs(hero) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
            },
        )

        Text(
            text = astronaut.name,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(hero.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
        )

        InfoChip(
            text = astronaut.agency?.abbrev ?: astronaut.agency?.name ?: "Агентство",
            modifier = Modifier.constrainAs(agencyChip) {
                top.linkTo(title.bottom, margin = 12.dp)
                centerHorizontallyTo(parent)
            },
        )

        Text(
            text = astronaut.status?.name ?: "Статус уточняется",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.constrainAs(statusText) {
                top.linkTo(agencyChip.bottom, margin = 8.dp)
                centerHorizontallyTo(parent)
            },
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .constrainAs(statRow) {
                    top.linkTo(statusText.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
        ) {
            StatBox(value = astronaut.flights_count?.toString() ?: "—", label = "Полёты", modifier = Modifier.weight(1f))
            StatBox(value = astronaut.spacewalks_count?.toString() ?: "—", label = "Выходы в космос", modifier = Modifier.weight(1f))
            StatBox(value = astronaut.age?.toString() ?: "—", label = "Возраст", modifier = Modifier.weight(1f))
        }

        if (!astronaut.bio.isNullOrBlank()) {
            SectionTitle(
                text = "Биография",
                modifier = Modifier.constrainAs(bioTitle) {
                    top.linkTo(statRow.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                },
            )
            Text(
                text = astronaut.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.constrainAs(bioText) {
                    top.linkTo(bioTitle.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            )
        }

        SectionTitle(
            text = "Детали",
            modifier = Modifier.constrainAs(fieldsTitle) {
                top.linkTo(
                    if (astronaut.bio.isNullOrBlank()) statRow.bottom else bioText.bottom,
                    margin = 24.dp,
                )
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
                DetailField(label = "Статус", value = astronaut.status?.name)
                DetailField(label = "Тип", value = astronaut.type?.name)
                DetailField(label = "Национальность", value = astronaut.nationality.firstOrNull()?.name)
                DetailField(label = "Дата рождения", value = formatIsoDate(astronaut.date_of_birth))
                DetailField(label = "Время в космосе", value = formatIsoDuration(astronaut.time_in_space))
                DetailField(label = "Время в EVA", value = formatIsoDuration(astronaut.eva_time))
                DetailField(label = "Первый полёт", value = formatIsoDate(astronaut.first_flight))
            }
        }
    }
}