package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.local.FavoriteEntity
import com.example.android_development_practices.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val favoriteJson = Json

internal fun favoriteSnapshot(value: FavoriteSource): FavoriteEntity = when (value) {
    is Launch -> FavoriteEntity("launch:${value.id}", "launch", value.name, value.providerName.orEmpty(), value.imageUrl, value.id, favoriteJson.encodeToString(value))
    is SpaceEvent -> FavoriteEntity("event:${value.id}", "event", value.name, value.location.orEmpty(), value.imageUrl, value.id.toString(), favoriteJson.encodeToString(value))
    is Expedition -> FavoriteEntity("expedition:${value.id}", "expedition", value.name, value.stationName.orEmpty(), value.patchImageUrl, value.id.toString(), favoriteJson.encodeToString(value))
    is Agency -> FavoriteEntity("agency:${value.id}", "agency", value.name, value.countries.joinToString(), value.imageUrl, value.id.toString(), favoriteJson.encodeToString(value))
    is Astronaut -> FavoriteEntity("astronaut:${value.id}", "astronaut", value.name, value.agencyName.orEmpty(), value.imageUrl, value.id.toString(), favoriteJson.encodeToString(value))
}
