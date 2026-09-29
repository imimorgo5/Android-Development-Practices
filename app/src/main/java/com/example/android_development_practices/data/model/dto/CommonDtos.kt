package com.example.android_development_practices.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T> = emptyList(),
)

@Serializable
data class ApiImage(
    val id: Int? = null,
    val name: String? = null,
    val image_url: String? = null,
    val thumbnail_url: String? = null,
    val credit: String? = null,
)

@Serializable
data class LaunchStatus(
    val id: Int? = null,
    val name: String? = null,
    val abbrev: String? = null,
    val description: String? = null,
)

@Serializable
data class NetPrecision(
    val id: Int? = null,
    val name: String? = null,
    val abbrev: String? = null,
    val description: String? = null,
)

@Serializable
data class AgencyType(
    val id: Int? = null,
    val name: String? = null,
)

@Serializable
data class EventType(
    val id: Int? = null,
    val name: String? = null,
)

@Serializable
data class AstronautStatus(
    val id: Int? = null,
    val name: String? = null,
)

@Serializable
data class AstronautRole(
    val id: Int? = null,
    val role: String? = null,
    val priority: Int? = null,
)

@Serializable
data class SpaceStationStatus(
    val id: Int? = null,
    val name: String? = null,
)

@Serializable
data class AstronautType(
    val id: Int? = null,
    val name: String? = null,
)

@Serializable
data class GeopoliticalCountry(
    val id: Int? = null,
    val name: String? = null,
    val alpha_2_code: String? = null,
)

/** Компактное описание агентства (поле agency / launch_service_provider). */
@Serializable
data class AgencyMini(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val abbrev: String? = null,
    val type: AgencyType? = null,
)

@Serializable
data class LauncherConfig(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val families: List<LauncherConfigFamily> = emptyList(),
    val full_name: String? = null,
    val variant: String? = null,
)

@Serializable
data class LauncherConfigFamily(
    val id: Int? = null,
    val name: String? = null,
)

@Serializable
data class Orbit(
    val id: Int? = null,
    val name: String? = null,
    val abbrev: String? = null,
)

@Serializable
data class Mission(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val orbit: Orbit? = null,
)

@Serializable
data class Pad(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val info_url: String? = null,
    val wiki_url: String? = null,
    val map_url: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val map_image: String? = null,
    val total_launch_count: Int? = null,
    val location: PadLocation? = null,
)

@Serializable
data class PadLocation(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val country: GeopoliticalCountry? = null,
    val total_launch_count: Int? = null,
)

@Serializable
data class ProgramNormal(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val description: String? = null,
    val image: ApiImage? = null,
    val info_url: String? = null,
    val wiki_url: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val agencies: List<AgencyMini> = emptyList(),
)