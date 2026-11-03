package com.example.android_development_practices.ui.event

import com.example.android_development_practices.domain.model.SpaceEvent
import com.example.android_development_practices.ui.common.formatIsoDate
import com.example.android_development_practices.ui.common.formatIsoDuration

data class EventListItemUiModel(
    val source: SpaceEvent,
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val thumbnailFallback: String,
    val subtitle: String,
)

data class EventDetailUiModel(
    val source: SpaceEvent,
    val name: String,
    val imageUrl: String?,
    val typeName: String?,
    val typeText: String,
    val date: String,
    val fieldDate: String,
    val webcast: String,
    val videoUrl: String?,
    val description: String,
    val location: String,
    val duration: String,
    val launchCount: String,
)

internal fun SpaceEvent.toListUiModel() = EventListItemUiModel(
    source = this,
    id = id,
    name = name,
    imageUrl = imageUrl,
    thumbnailFallback = typeName ?: "Событие",
    subtitle = "${formatIsoDate(date, fallback = "дата уточняется")} · ${location ?: "место не указано"}",
)

internal fun SpaceEvent.toDetailUiModel() = EventDetailUiModel(
    source = this,
    name = name,
    imageUrl = imageUrl,
    typeName = typeName,
    typeText = typeName ?: "—",
    date = formatIsoDate(date, fallback = "—"),
    fieldDate = formatIsoDate(date),
    webcast = when { webcastLive == true -> "В эфире"; videoUrl != null -> "Запись"; else -> "—" },
    videoUrl = videoUrl,
    description = description ?: "Описание события уточняется.",
    location = location ?: "—",
    duration = formatIsoDuration(duration),
    launchCount = launchCount.toString(),
)
