package com.example.android_development_practices.ui.agency

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
import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.ui.common.DetailField
import com.example.android_development_practices.ui.common.DetailHero
import com.example.android_development_practices.ui.common.InfoChip
import com.example.android_development_practices.ui.common.ScreenScaffold
import com.example.android_development_practices.ui.common.SectionTitle
import com.example.android_development_practices.ui.common.StatBox
import com.example.android_development_practices.ui.common.UiState
import com.example.android_development_practices.ui.common.UiStateContent

@Composable
fun AgencyDetailRoute(
    id: String,
    onBack: () -> Unit,
    viewModel: AgencyDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    AgencyDetailScreen(uiState = uiState, onBack = onBack, onRetry = viewModel::retry)
}

@Composable
private fun AgencyDetailScreen(
    uiState: UiState<Agency>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    ScreenScaffold(title = "Детали агентства", onBack = onBack) { contentModifier ->
        UiStateContent(uiState = uiState, onRetry = onRetry, modifier = contentModifier) { agency ->
            AgencyDetailContent(agency = agency)
        }
    }
}

@Composable
private fun AgencyDetailContent(agency: Agency) {
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
        val descTitle = createRef()
        val descText = createRef()
        val fieldsTitle = createRef()
        val fieldsCard = createRef()

        DetailHero(
            emoji = agency.abbreviation ?: "🏛️",
            imageUrl = agency.imageUrl,
            modifier = Modifier.constrainAs(hero) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
            },
        )

        Text(
            text = agency.name,
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
            text = agency.typeName ?: "Агентство",
            modifier = Modifier.constrainAs(typeChip) {
                top.linkTo(title.bottom, margin = 12.dp)
                centerHorizontallyTo(parent)
            },
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .constrainAs(statRow) {
                    top.linkTo(typeChip.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
        ) {
            StatBox(
                value = agency.totalLaunchCount?.toString() ?: "—",
                label = "Всего запусков",
                modifier = Modifier.weight(1f),
            )
            StatBox(
                value = agency.successfulLaunchCount?.toString() ?: "—",
                label = "Успешных",
                modifier = Modifier.weight(1f),
            )
        }

        SectionTitle(
            text = "Об агентстве",
            modifier = Modifier.constrainAs(descTitle) {
                top.linkTo(statRow.bottom, margin = 24.dp)
                start.linkTo(parent.start)
            },
        )

        Text(
            text = agency.description ?: "Описание агентства уточняется.",
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
                DetailField(label = "Аббревиатура", value = agency.abbreviation)
                DetailField(label = "Тип", value = agency.typeName)
                DetailField(label = "Страны", value = agency.countries.joinToString().takeIf { it.isNotBlank() })
                DetailField(label = "Руководитель", value = agency.administrator)
                DetailField(label = "Год основания", value = agency.foundingYear?.toString())
                DetailField(label = "Сайт", value = agency.website)
            }
        }
    }
}
