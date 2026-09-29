package com.example.android_development_practices.domain.model

data class Launch(
    val id: String,
    val name: String,
    val status: String?,
    val net: String?,
    val netPrecision: String?,
    val windowStart: String?,
    val imageUrl: String?,
    val probability: Int?,
    val providerName: String?,
    val rocketName: String?,
    val rocketFamily: String?,
    val rocketLaunchCount: Int?,
    val missionName: String?,
    val missionDescription: String?,
    val orbitName: String?,
    val padName: String?,
    val padLocationName: String?,
)

data class SpaceEvent(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val typeName: String?,
    val date: String?,
    val webcastLive: Boolean?,
    val videoUrl: String?,
    val description: String?,
    val location: String?,
    val duration: String?,
    val launchCount: Int,
)

data class Expedition(
    val id: Int,
    val name: String,
    val start: String?,
    val end: String?,
    val stationName: String?,
    val stationDescription: String?,
    val patchImageUrl: String?,
    val patchName: String?,
    val patchCount: Int,
    val spacewalkCount: Int,
    val crewCount: Int,
    val commanderName: String?,
)

data class Agency(
    val id: Int,
    val name: String,
    val abbreviation: String?,
    val typeName: String?,
    val countries: List<String>,
    val imageUrl: String?,
    val description: String?,
    val totalLaunchCount: Int?,
    val successfulLaunchCount: Int?,
    val administrator: String?,
    val foundingYear: Int?,
    val website: String?,
)

data class Astronaut(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val statusName: String?,
    val typeName: String?,
    val agencyName: String?,
    val agencyAbbreviation: String?,
    val inSpace: Boolean?,
    val age: Int?,
    val birthDate: String?,
    val nationalities: List<String>,
    val biography: String?,
    val timeInSpace: String?,
    val evaTime: String?,
    val firstFlight: String?,
)
