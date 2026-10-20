package com.example.android_development_practices.domain

import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.domain.model.SpaceEvent
import com.example.android_development_practices.domain.repository.SpaceRepository
import javax.inject.Inject

class SpaceUseCases @Inject constructor(private val repository: SpaceRepository) {
    suspend fun catalogFilterOptions(catalog: String) = repository.getFilterOptions(catalog)
    suspend fun upcomingLaunches(search: String? = null, agencyId: Int? = null, year: Int? = null, statusId: Int? = null): List<Launch> =
        repository.getUpcomingLaunches(search, agencyId, year, statusId)
    suspend fun launch(id: String): Launch = repository.getLaunchDetail(id)
    suspend fun upcomingEvents(search: String? = null, agencyId: Int? = null, year: Int? = null, typeId: Int? = null): List<SpaceEvent> =
        repository.getUpcomingEvents(search, agencyId, year, typeId)
    suspend fun event(id: Int): SpaceEvent = repository.getEventDetail(id)
    suspend fun expeditions(search: String? = null, agencyId: Int? = null, startGte: String? = null, endLte: String? = null, stationId: Int? = null): List<Expedition> =
        repository.getExpeditions(search, agencyId, startGte, endLte, stationId)
    suspend fun expedition(id: Int): Expedition = repository.getExpeditionDetail(id)
    suspend fun agencies(search: String? = null, countryCode: String? = null, typeId: Int? = null): List<Agency> =
        repository.getAgencies(search, countryCode, typeId)
    suspend fun agency(id: Int): Agency = repository.getAgencyDetail(id)
    suspend fun astronauts(search: String? = null, nationality: String? = null, agencyId: Int? = null, statusId: Int? = null): List<Astronaut> =
        repository.getAstronauts(search, nationality, agencyId, statusId)
    suspend fun astronaut(id: Int): Astronaut = repository.getAstronautDetail(id)
}
