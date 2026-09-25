package com.example.android_development_practices.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class Launch(
    val id: String = "",
    val url: String? = null,
    val name: String = "",
    val slug: String? = null,
    val launch_designator: String? = null,
    val status: LaunchStatus? = null,
    val last_updated: String? = null,
    val net: String? = null,
    val net_precision: NetPrecision? = null,
    val window_start: String? = null,
    val window_end: String? = null,
    val image: ApiImage? = null,
    val infographic: String? = null,
    val probability: Int? = null,
    val weather_concerns: String? = null,
    val failreason: String? = null,
    val hashtag: String? = null,
    val webcast_live: Boolean? = null,
    val launch_service_provider: AgencyMini? = null,
    val rocket: Rocket? = null,
    val mission: Mission? = null,
    val pad: Pad? = null,
    val program: List<ProgramNormal> = emptyList(),
    val orbital_launch_attempt_count: Int? = null,
    val pad_launch_attempt_count: Int? = null,
    val agency_launch_attempt_count: Int? = null,
)

@Serializable
data class Rocket(
    val id: Int? = null,
    val configuration: LauncherConfig? = null,
)