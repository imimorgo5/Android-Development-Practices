package com.example.android_development_practices.domain.repository

import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.domain.model.SpaceEvent
import com.example.android_development_practices.domain.model.FilterOption

interface SpaceRepository {
    suspend fun getFilterOptions(catalog: String): Map<String, List<FilterOption>>
    suspend fun getUpcomingLaunches(search: String?, agencyId: Int?, year: Int?, statusId: Int?): List<Launch>
    suspend fun getLaunchDetail(id: String): Launch
    suspend fun getUpcomingEvents(search: String?, agencyId: Int?, year: Int?, typeId: Int?): List<SpaceEvent>
    suspend fun getEventDetail(id: Int): SpaceEvent
    suspend fun getExpeditions(search: String?, agencyId: Int?, startGte: String?, endLte: String?, stationId: Int?): List<Expedition>
    suspend fun getExpeditionDetail(id: Int): Expedition
    suspend fun getAgencies(search: String?, countryCode: String?, typeId: Int?): List<Agency>
    suspend fun getAgencyDetail(id: Int): Agency
    suspend fun getAstronauts(search: String?, nationality: String?, agencyId: Int?, statusId: Int?): List<Astronaut>
    suspend fun getAstronautDetail(id: Int): Astronaut
}
