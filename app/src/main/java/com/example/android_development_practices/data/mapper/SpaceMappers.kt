package com.example.android_development_practices.data.mapper

import com.example.android_development_practices.data.model.dto.Agency as AgencyDto
import com.example.android_development_practices.data.model.dto.Astronaut as AstronautDto
import com.example.android_development_practices.data.model.dto.Expedition as ExpeditionDto
import com.example.android_development_practices.data.model.dto.Launch as LaunchDto
import com.example.android_development_practices.data.model.dto.SpaceEvent as SpaceEventDto
import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.domain.model.SpaceEvent

internal fun LaunchDto.toDomain() = Launch(
    id = id,
    name = name,
    status = status?.name ?: status?.abbrev,
    net = net,
    netPrecision = net_precision?.name ?: net_precision?.abbrev,
    windowStart = window_start,
    imageUrl = image?.image_url,
    probability = probability,
    providerName = launch_service_provider?.name,
    rocketName = rocket?.configuration?.full_name ?: rocket?.configuration?.name,
    rocketFamily = rocket?.configuration?.families?.firstOrNull()?.name,
    rocketLaunchCount = rocket?.configuration?.total_launch_count,
    missionName = mission?.name,
    missionDescription = mission?.description,
    orbitName = mission?.orbit?.abbrev ?: mission?.orbit?.name,
    padName = pad?.name,
    padLocationName = pad?.location?.name,
)

internal fun SpaceEventDto.toDomain() = SpaceEvent(
    id = id,
    name = name,
    imageUrl = image?.image_url,
    typeName = type?.name,
    date = date,
    webcastLive = webcast_live,
    videoUrl = vid_urls.firstNotNullOfOrNull { it.url?.takeIf(String::isNotBlank) },
    description = description,
    location = location,
    duration = duration,
    launchCount = launches.size,
)

internal fun ExpeditionDto.toDomain() = Expedition(
    id = id,
    name = name,
    start = start,
    end = end,
    stationName = spacestation?.name,
    stationDescription = spacestation?.description,
    patchImageUrl = mission_patches.firstOrNull()?.image_url,
    patchName = mission_patches.firstOrNull()?.name,
    patchCount = mission_patches.size,
    spacewalkCount = spacewalks.size,
    crewCount = crew.size,
    commanderName = crew.firstOrNull { it.role?.role.equals("Commander", ignoreCase = true) }
        ?.astronaut?.name,
)

internal fun AgencyDto.toDomain() = Agency(
    id = id,
    name = name,
    abbreviation = abbrev,
    typeName = type?.name,
    countries = country.mapNotNull { it.name?.takeIf(String::isNotBlank) },
    imageUrl = image?.image_url,
    description = description,
    totalLaunchCount = total_launch_count,
    successfulLaunchCount = successful_launches,
    administrator = administrator,
    foundingYear = founding_year,
    website = info_url,
)

internal fun AstronautDto.toDomain() = Astronaut(
    id = id,
    name = name,
    imageUrl = image?.image_url,
    statusName = status?.name,
    typeName = type?.name,
    agencyName = agency?.name,
    agencyAbbreviation = agency?.abbrev,
    inSpace = in_space,
    age = age,
    birthDate = date_of_birth,
    nationalities = nationality.mapNotNull { it.name?.takeIf(String::isNotBlank) },
    biography = bio,
    timeInSpace = time_in_space,
    evaTime = eva_time,
    firstFlight = first_flight,
)
