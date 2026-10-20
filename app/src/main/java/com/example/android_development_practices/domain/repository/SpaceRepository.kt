package com.example.android_development_practices.domain.repository

import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.domain.model.SpaceEvent

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
