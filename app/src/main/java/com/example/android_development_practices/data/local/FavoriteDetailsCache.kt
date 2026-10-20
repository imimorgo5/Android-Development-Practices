package com.example.android_development_practices.data.local

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import com.example.android_development_practices.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteDetailsCache @Inject constructor(private val dao: FavoriteDao) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun <T> load(
        category: String,
        id: String,
        serializer: KSerializer<T>,
        fetch: suspend () -> T,
    ): T {
        val key = "$category:$id"
        return try {
            fetch().also { value ->
                if (dao.contains(key)) dao.updateDetail(key, json.encodeToString(serializer, value))
            }
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            val favorite = dao.get(key) ?: throw error
            val payload = favorite.detailPayload ?: fallbackPayload(favorite) ?: throw error
            if (favorite.detailPayload == null) dao.updateDetail(key, payload)
            json.decodeFromString(serializer, payload)
        }
    }

    private fun fallbackPayload(item: FavoriteEntity): String? {
        return when (item.category) {
        "launch" -> json.encodeToString(Launch.serializer(), Launch(
            id = item.detailId, name = item.title, status = null, net = null, netPrecision = null,
            windowStart = null, imageUrl = item.imageUrl, probability = null, providerName = item.subtitle,
            rocketName = null, rocketFamily = null, rocketLaunchCount = null, missionName = null,
            missionDescription = null, orbitName = null, padName = null, padLocationName = null,
        ))
        "event" -> json.encodeToString(SpaceEvent.serializer(), SpaceEvent(
            id = item.detailId.toIntOrNull() ?: return null, name = item.title, imageUrl = item.imageUrl,
            typeName = null, date = null, webcastLive = null, videoUrl = null, description = null,
            location = item.subtitle, duration = null, launchCount = 0,
        ))
        "expedition" -> json.encodeToString(Expedition.serializer(), Expedition(
            id = item.detailId.toIntOrNull() ?: return null, name = item.title, start = null, end = null,
            stationName = item.subtitle, stationDescription = null, patchImageUrl = item.imageUrl,
            patchName = null, patchCount = 0, spacewalkCount = 0, crewCount = 0, commanderName = null,
        ))
        "agency" -> json.encodeToString(Agency.serializer(), Agency(
            id = item.detailId.toIntOrNull() ?: return null, name = item.title, abbreviation = null,
            typeName = null, countries = item.subtitle.split(",").map(String::trim).filter(String::isNotBlank),
            imageUrl = item.imageUrl, description = null, totalLaunchCount = null,
            successfulLaunchCount = null, administrator = null, foundingYear = null, website = null,
        ))
        "astronaut" -> json.encodeToString(Astronaut.serializer(), Astronaut(
            id = item.detailId.toIntOrNull() ?: return null, name = item.title, imageUrl = item.imageUrl,
            statusName = null, typeName = null, agencyName = item.subtitle, agencyAbbreviation = null,
            inSpace = null, age = null, birthDate = null, nationalities = emptyList(), biography = null,
            timeInSpace = null, evaTime = null, firstFlight = null,
        ))
        else -> null
        }
    }
}
