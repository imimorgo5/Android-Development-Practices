package com.example.android_development_practices.ui.event

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_development_practices.domain.model.SpaceEvent
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
fun EventDetailRoute(
    id: String,
    onBack: () -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    EventDetailScreen(uiState = uiState, onBack = onBack, onRetry = viewModel::retry)
}

@Composable
private fun EventDetailScreen(
    uiState: UiState<SpaceEvent>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    ScreenScaffold(title = "Детали события", onBack = onBack) { contentModifier ->
        UiStateContent(uiState = uiState, onRetry = onRetry, modifier = contentModifier) { event ->
            EventDetailContent(event = event)
        }
    }
}

@Composable
private fun EventDetailContent(event: SpaceEvent) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        val hero = createRef()
        val title = createRef()
        val typeChip = createRef()
        val statRow = createRef()
        val webcastLink = createRef()
        val descTitle = createRef()
        val descText = createRef()
        val fieldsTitle = createRef()
        val fieldsCard = createRef()

        DetailHero(
            emoji = "📡",
            imageUrl = event.imageUrl,
            modifier = Modifier.constrainAs(hero) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
            },
        )

        Text(
            text = event.name,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(hero.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
        )

        event.typeName?.let { type ->
            InfoChip(
                text = type,
                modifier = Modifier.constrainAs(typeChip) {
                    top.linkTo(title.bottom, margin = 12.dp)
                    centerHorizontallyTo(parent)
                },
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .constrainAs(statRow) {
                    top.linkTo(if (event.typeName != null) typeChip.bottom else title.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
        ) {
            StatBox(
                value = formatIsoDate(event.date, fallback = "—"),
                label = "Дата",
                modifier = Modifier.weight(1f),
            )
            StatBox(
                value = when {
                    event.webcastLive == true -> "В эфире"
                    event.videoUrl != null -> "Запись"
                    else -> "—"
                },
                label = "Трансляция",
                modifier = Modifier.weight(1f),
            )
        }

        val webcast = event.videoUrl
        if (webcast != null) {
            val uriHandler = LocalUriHandler.current
            Text(
                text = "▶ Смотреть трансляцию",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .constrainAs(webcastLink) {
                        top.linkTo(statRow.bottom, margin = 12.dp)
                        start.linkTo(parent.start)
                    }
                    .clickable { uriHandler.openUri(webcast) },
            )
        }

        SectionTitle(
            text = "Описание",
            modifier = Modifier.constrainAs(descTitle) {
                top.linkTo(
                    if (webcast != null) webcastLink.bottom else statRow.bottom,
                    margin = 24.dp,
                )
                start.linkTo(parent.start)
            },
        )

        Text(
            text = event.description ?: "Описание события уточняется.",
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
                DetailField(label = "Тип", value = event.typeName ?: "—")
                DetailField(label = "Дата", value = formatIsoDate(event.date))
                DetailField(label = "Место", value = event.location ?: "—")
                DetailField(label = "Продолжительность", value = formatIsoDuration(event.duration))
                DetailField(label = "Связанных запусков", value = event.launchCount.toString())
            }
        }
    }
}
