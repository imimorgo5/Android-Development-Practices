package com.example.android_development_practices.ui.expedition

import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.ui.common.formatIsoDate

data class ExpeditionListItemUiModel(
    val source: Expedition,
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val thumbnailFallback: String,
    val subtitle: String,
)

data class ExpeditionDetailUiModel(
    val source: Expedition,
    val name: String,
    val imageUrl: String?,
    val stationName: String,
    val stationDescription: String?,
    val crewCount: String,
    val patchCount: String,
    val start: String,
    val end: String,
    val spacewalkCount: String,
    val patchName: String?,
    val commanderName: String?,
)

internal fun Expedition.toListUiModel() = ExpeditionListItemUiModel(
    source = this,
    id = id,
    name = name,
    imageUrl = patchImageUrl,
    thumbnailFallback = stationName?.take(3) ?: "МКС",
    subtitle = "${stationName ?: "станция не указана"} · с ${formatIsoDate(start, fallback = "—")}",
)

internal fun Expedition.toDetailUiModel() = ExpeditionDetailUiModel(
    source = this,
    name = name,
    imageUrl = patchImageUrl,
    stationName = stationName ?: "—",
    stationDescription = stationDescription,
    crewCount = "$crewCount чел.",
    patchCount = patchCount.toString(),
    start = formatIsoDate(start),
    end = formatIsoDate(end, fallback = "идёт сейчас"),
    spacewalkCount = spacewalkCount.toString(),
    patchName = patchName,
    commanderName = commanderName,
)
