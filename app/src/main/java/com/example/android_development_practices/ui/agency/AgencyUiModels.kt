package com.example.android_development_practices.ui.agency

import com.example.android_development_practices.domain.model.Agency

data class AgencyListItemUiModel(
    val source: Agency,
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val thumbnailFallback: String,
    val subtitle: String,
)

data class AgencyDetailUiModel(
    val source: Agency,
    val name: String,
    val imageUrl: String?,
    val heroFallback: String,
    val typeChip: String,
    val typeName: String?,
    val abbreviation: String?,
    val totalLaunchCount: String,
    val successfulLaunchCount: String,
    val description: String,
    val countries: String?,
    val administrator: String?,
    val foundingYear: String?,
    val website: String?,
)

internal fun Agency.toListUiModel() = AgencyListItemUiModel(
    source = this,
    id = id,
    name = name,
    imageUrl = imageUrl,
    thumbnailFallback = abbreviation ?: name.take(3),
    subtitle = "${countries.joinToString().ifBlank { "—" }} · ${typeName ?: "агентство"}",
)

internal fun Agency.toDetailUiModel() = AgencyDetailUiModel(
    source = this,
    name = name,
    imageUrl = imageUrl,
    heroFallback = abbreviation ?: "🏛️",
    typeChip = typeName ?: "Агентство",
    typeName = typeName,
    abbreviation = abbreviation,
    totalLaunchCount = totalLaunchCount?.toString() ?: "—",
    successfulLaunchCount = successfulLaunchCount?.toString() ?: "—",
    description = description ?: "Описание агентства уточняется.",
    countries = countries.joinToString().takeIf(String::isNotBlank),
    administrator = administrator,
    foundingYear = foundingYear?.toString(),
    website = website,
)
