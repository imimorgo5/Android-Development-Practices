package com.example.android_development_practices.domain

import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.domain.model.SpaceEvent
import com.example.android_development_practices.domain.repository.SpaceRepository
import javax.inject.Inject

class SpaceUseCases @Inject constructor(private val repository: SpaceRepository) {
    suspend fun upcomingLaunches(): List<Launch> = repository.getUpcomingLaunches()
    suspend fun launch(id: String): Launch = repository.getLaunchDetail(id)
    suspend fun upcomingEvents(): List<SpaceEvent> = repository.getUpcomingEvents()
    suspend fun event(id: Int): SpaceEvent = repository.getEventDetail(id)
    suspend fun expeditions(): List<Expedition> = repository.getExpeditions()
    suspend fun expedition(id: Int): Expedition = repository.getExpeditionDetail(id)
    suspend fun agencies(): List<Agency> = repository.getAgencies()
    suspend fun agency(id: Int): Agency = repository.getAgencyDetail(id)
    suspend fun astronauts(): List<Astronaut> = repository.getAstronauts()
    suspend fun astronaut(id: Int): Astronaut = repository.getAstronautDetail(id)
}
