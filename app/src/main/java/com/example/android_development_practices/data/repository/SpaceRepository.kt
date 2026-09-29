package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.mapper.toDomain
import com.example.android_development_practices.data.remote.SpaceApi
import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.domain.model.SpaceEvent
import com.example.android_development_practices.domain.repository.SpaceRepository
import javax.inject.Inject

class SpaceRepositoryImpl @Inject constructor(private val api: SpaceApi) : SpaceRepository {
    override suspend fun getUpcomingLaunches() = api.upcomingLaunches().results.map { it.toDomain() }
    override suspend fun getLaunchDetail(id: String) = api.launch(id).toDomain()
    override suspend fun getUpcomingEvents() = api.upcomingEvents().results.map { it.toDomain() }
    override suspend fun getEventDetail(id: Int) = api.event(id).toDomain()
    override suspend fun getExpeditions() = api.expeditions().results.map { it.toDomain() }
    override suspend fun getExpeditionDetail(id: Int) = api.expedition(id).toDomain()
    override suspend fun getAgencies(): List<Agency> {
        val agencies = mutableListOf<Agency>()
        var offset = 0
        var totalCount: Int
        do {
            val page = api.agencies(offset = offset)
            totalCount = page.count
            if (page.results.isEmpty()) break
            agencies += page.results.map { it.toDomain() }
            offset += page.results.size
        } while (offset < totalCount)
        return agencies
    }
    override suspend fun getAgencyDetail(id: Int) = api.agency(id).toDomain()
    override suspend fun getAstronauts() = api.astronauts().results.map { it.toDomain() }
    override suspend fun getAstronautDetail(id: Int) = api.astronaut(id).toDomain()
}
