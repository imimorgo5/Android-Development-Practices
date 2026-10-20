package com.example.android_development_practices.ui.expedition

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
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.ui.common.DetailField
import com.example.android_development_practices.ui.common.DetailHero
import com.example.android_development_practices.ui.common.InfoChip
import com.example.android_development_practices.ui.common.ScreenScaffold
import com.example.android_development_practices.ui.common.SectionTitle
import com.example.android_development_practices.ui.common.StatBox
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.ui.common.UiStateContent
import com.example.android_development_practices.ui.common.formatIsoDate

@Composable
fun ExpeditionDetailRoute(
    id: String,
    onBack: () -> Unit,
    viewModel: ExpeditionDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    ExpeditionDetailScreen(uiState = uiState, onBack = onBack, onRetry = viewModel::retry)
}

@Composable
private fun ExpeditionDetailScreen(
    uiState: UiState<Expedition>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    ScreenScaffold(title = "Детали экспедиции", onBack = onBack) { contentModifier ->
        UiStateContent(uiState = uiState, onRetry = onRetry, modifier = contentModifier) { expedition ->
            ExpeditionDetailContent(expedition = expedition)
        }
    }
}

@Composable
private fun ExpeditionDetailContent(expedition: Expedition) {
    val station = expedition.stationName ?: "—"

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        val hero = createRef()
        val title = createRef()
        val stationChip = createRef()
        val statRow = createRef()
        val stationTitle = createRef()
        val stationText = createRef()
        val fieldsTitle = createRef()
        val fieldsCard = createRef()

        DetailHero(
            emoji = "🛰️",
            imageUrl = expedition.patchImageUrl,
            modifier = Modifier.constrainAs(hero) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
            },
        )

        Text(
            text = expedition.name,
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
            text = station,
            modifier = Modifier.constrainAs(stationChip) {
                top.linkTo(title.bottom, margin = 12.dp)
                centerHorizontallyTo(parent)
            },
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .constrainAs(statRow) {
                    top.linkTo(stationChip.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
        ) {
            StatBox(
                value = "${expedition.crewCount} чел.",
                label = "Экипаж",
                modifier = Modifier.weight(1f),
            )
            StatBox(
                value = expedition.patchCount.toString(),
                label = "Эмблемы",
                modifier = Modifier.weight(1f),
            )
        }

        if (expedition.stationDescription != null) {
            SectionTitle(
                text = "О станции",
                modifier = Modifier.constrainAs(stationTitle) {
                    top.linkTo(statRow.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                },
            )
            Text(
                text = expedition.stationDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.constrainAs(stationText) {
                    top.linkTo(stationTitle.bottom, margin = 8.dp)
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
                    if (expedition.stationDescription != null) stationText.bottom else statRow.bottom,
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
                DetailField(label = "Станция", value = station)
                DetailField(label = "Начало", value = formatIsoDate(expedition.start))
                DetailField(label = "Конец", value = formatIsoDate(expedition.end, fallback = "идёт сейчас"))
                DetailField(label = "Выходов в космос", value = expedition.spacewalkCount.toString())
                DetailField(label = "Эмблема", value = expedition.patchName)
                DetailField(
                    label = "Командир",
                    value = expedition.commanderName,
                )
            }
        }
    }
}
