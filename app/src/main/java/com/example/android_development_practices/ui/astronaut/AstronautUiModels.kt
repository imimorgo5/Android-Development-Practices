package com.example.android_development_practices.ui.astronaut

import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.ui.common.formatIsoDate
import com.example.android_development_practices.ui.common.formatIsoDuration

data class AstronautListItemUiModel(
    val source: Astronaut,
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val thumbnailFallback: String,
    val subtitle: String,
)

data class AstronautDetailUiModel(
    val source: Astronaut,
    val name: String,
    val imageUrl: String?,
    val heroFallback: String,
    val agencyChip: String,
    val statusText: String,
    val statusName: String?,
    val typeName: String?,
    val timeInSpace: String,
    val evaTime: String,
    val biography: String?,
    val age: String?,
    val inSpace: String?,
    val nationalities: String?,
    val birthDate: String,
    val firstFlight: String,
)

internal fun Astronaut.toListUiModel() = AstronautListItemUiModel(
    source = this,
    id = id,
    name = name,
    imageUrl = imageUrl,
    thumbnailFallback = initials(),
    subtitle = listOf(agencyAbbreviation.orEmpty(), statusName.orEmpty()).filter(String::isNotBlank).joinToString(" · ").ifBlank { "—" },
)

internal fun Astronaut.toDetailUiModel() = AstronautDetailUiModel(
    source = this,
    name = name,
    imageUrl = imageUrl,
    heroFallback = initials(),
    agencyChip = agencyAbbreviation ?: agencyName ?: "Агентство",
    statusText = statusName ?: "Статус уточняется",
    statusName = statusName,
    typeName = typeName,
    timeInSpace = formatIsoDuration(timeInSpace),
    evaTime = formatIsoDuration(evaTime),
    biography = biography?.takeIf(String::isNotBlank),
    age = age?.toString(),
    inSpace = inSpace?.let { if (it) "Да" else "Нет" },
    nationalities = nationalities.joinToString().takeIf(String::isNotBlank),
    birthDate = formatIsoDate(birthDate),
    firstFlight = formatIsoDate(firstFlight),
)

private fun Astronaut.initials(): String = name.split(" ")
    .mapNotNull { it.firstOrNull()?.toString() }
    .takeLast(2)
    .joinToString("")
    .ifBlank { "🧑‍🚀" }
