package com.example.android_development_practices.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class Expedition(
    val id: Int = 0,
    val url: String? = null,
    val name: String = "",
    val start: String? = null,
    val end: String? = null,
    val response_mode: String? = null,
    val spacestation: SpaceStationNormal? = null,
    val mission_patches: List<MissionPatch> = emptyList(),
    val spacewalks: List<SpacewalkNormal> = emptyList(),
    val crew: List<AstronautFlight> = emptyList(),
)

@Serializable
data class SpaceStationNormal(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val image: ApiImage? = null,
    val status: SpaceStationStatus? = null,
    val founded: String? = null,
    val description: String? = null,
    val orbit: String? = null,
)

@Serializable
data class MissionPatch(
    val id: Int? = null,
    val name: String? = null,
    val priority: Int? = null,
    val image_url: String? = null,
    val agency: AgencyMini? = null,
)

@Serializable
data class SpacewalkNormal(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val start: String? = null,
    val end: String? = null,
    val duration: String? = null,
)

/** Роль конкретного астронавта в составе экспедиции (поле crew). */
@Serializable
data class AstronautFlight(
    val id: Int? = null,
    val role: AstronautRole? = null,
    val astronaut: AstronautMini? = null,
)

/** Астронавт в составе экипажа (подмножество полей AstronautDetailed). */
@Serializable
data class AstronautMini(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val nationality: List<GeopoliticalCountry> = emptyList(),
    val image: ApiImage? = null,
)