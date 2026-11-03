package com.example.android_development_practices.ui.launch

import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.ui.common.formatIsoDate

data class LaunchListItemUiModel(
    val source: Launch,
    val id: String,
    val name: String,
    val imageUrl: String?,
    val thumbnailFallback: String,
    val subtitle: String,
)

data class LaunchDetailUiModel(
    val source: Launch,
    val name: String,
    val imageUrl: String?,
    val rocketFamily: String,
    val status: String,
    val launchDate: String,
    val rocketLaunchCount: String,
    val description: String,
    val provider: String,
    val rocket: String,
    val mission: String,
    val orbit: String,
    val pad: String,
    val window: String,
)

internal fun Launch.toListUiModel() = LaunchListItemUiModel(
    source = this,
    id = id,
    name = name,
    imageUrl = imageUrl,
    thumbnailFallback = rocketFamily?.take(3) ?: providerName?.take(3) ?: "🚀",
    subtitle = "${providerName ?: "—"} · ${formatIsoDate(net, fallback = "дата уточняется")}",
)

internal fun Launch.toDetailUiModel() = LaunchDetailUiModel(
    source = this,
    name = name,
    imageUrl = imageUrl,
    rocketFamily = rocketFamily ?: "🚀",
    status = status ?: "Статус уточняется",
    launchDate = formatIsoDate(net, fallback = "—"),
    rocketLaunchCount = rocketLaunchCount?.toString() ?: "—",
    description = missionDescription ?: "Описание миссии уточняется.",
    provider = providerName ?: "—",
    rocket = rocketName ?: "—",
    mission = missionName ?: "—",
    orbit = orbitName ?: "—",
    pad = listOfNotNull(padName, padLocationName).joinToString(" · ").ifBlank { "—" },
    window = windowStart?.let { formatIsoDate(it) } ?: "—",
)
