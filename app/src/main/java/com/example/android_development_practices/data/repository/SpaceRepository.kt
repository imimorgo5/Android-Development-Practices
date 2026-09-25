package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.model.dto.Agency
import com.example.android_development_practices.data.model.dto.Astronaut
import com.example.android_development_practices.data.model.dto.Expedition
import com.example.android_development_practices.data.model.dto.Launch
import com.example.android_development_practices.data.model.dto.SpaceEvent

interface SpaceRepository {
    suspend fun getUpcomingLaunches(): List<Launch>
    suspend fun getLaunchDetail(id: String): Launch

    suspend fun getUpcomingEvents(): List<SpaceEvent>
    suspend fun getEventDetail(id: Int): SpaceEvent

    suspend fun getExpeditions(): List<Expedition>
    suspend fun getExpeditionDetail(id: Int): Expedition

    suspend fun getAgencies(): List<Agency>
    suspend fun getAgencyDetail(id: Int): Agency

    suspend fun getAstronauts(): List<Astronaut>
    suspend fun getAstronautDetail(id: Int): Astronaut
}

/** Реализация на моковых данных. */
class MockSpaceRepository : SpaceRepository {

    override suspend fun getUpcomingLaunches(): List<Launch> = MockSpaceData.launches

    override suspend fun getLaunchDetail(id: String): Launch =
        MockSpaceData.launches.firstOrNull { it.id == id }
            ?: MockSpaceData.launches.first()

    override suspend fun getUpcomingEvents(): List<SpaceEvent> = MockSpaceData.events

    override suspend fun getEventDetail(id: Int): SpaceEvent =
        MockSpaceData.events.firstOrNull { it.id == id }
            ?: MockSpaceData.events.first()

    override suspend fun getExpeditions(): List<Expedition> = MockSpaceData.expeditions

    override suspend fun getExpeditionDetail(id: Int): Expedition =
        MockSpaceData.expeditions.firstOrNull { it.id == id }
            ?: MockSpaceData.expeditions.first()

    override suspend fun getAgencies(): List<Agency> = MockSpaceData.agencies

    override suspend fun getAgencyDetail(id: Int): Agency =
        MockSpaceData.agencies.firstOrNull { it.id == id }
            ?: MockSpaceData.agencies.first()

    override suspend fun getAstronauts(): List<Astronaut> = MockSpaceData.astronauts

    override suspend fun getAstronautDetail(id: Int): Astronaut =
        MockSpaceData.astronauts.firstOrNull { it.id == id }
            ?: MockSpaceData.astronauts.first()
}