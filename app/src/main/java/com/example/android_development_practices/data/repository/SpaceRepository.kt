package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.mapper.toDomain
import com.example.android_development_practices.data.model.dto.PaginatedResponse
import com.example.android_development_practices.data.remote.SpaceApi
import com.example.android_development_practices.domain.model.Agency
import com.example.android_development_practices.domain.model.Astronaut
import com.example.android_development_practices.domain.model.Expedition
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.domain.model.SpaceEvent
import com.example.android_development_practices.domain.model.FilterOption
import com.example.android_development_practices.domain.repository.SpaceRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class SpaceRepositoryImpl @Inject constructor(private val api: SpaceApi) : SpaceRepository {
    override suspend fun getFilterOptions(catalog: String): Map<String, List<FilterOption>> {
        suspend fun <T> pages(request: suspend (Int, Int) -> PaginatedResponse<T>): List<T> = fetchAllPages(request)
        suspend fun agencies() = pages { offset, limit -> api.agencies(offset = offset, limit = limit) }
        suspend fun optionsFromAgencies() = agencies().mapNotNull { agency ->
            val id = agency.id
            val name = agency.name.takeIf(String::isNotBlank)
            if (id == null || name == null) null else FilterOption(name, id.toString())
        }.distinctBy(FilterOption::value)

        return when (catalog) {
            "launch" -> mapOf(
                "lsp__id" to optionsFromAgencies(),
                "status__ids" to pages { offset, limit -> api.launchStatuses(limit, offset) }
                    .mapNotNull { it.id?.let { id -> it.name?.let { name -> FilterOption(name, id.toString()) } } },
            )
            "event" -> mapOf(
                "agency__ids" to optionsFromAgencies(),
                "type__ids" to pages { offset, limit -> api.eventTypes(limit, offset) }
                    .mapNotNull { it.id?.let { id -> it.name?.let { name -> FilterOption(name, id.toString()) } } },
            )
            "expedition" -> mapOf(
                "crew__astronaut__agency" to optionsFromAgencies(),
                "space_station" to pages { offset, limit -> api.spaceStations(limit, offset) }
                    .mapNotNull { it.id?.let { id -> it.name?.let { name -> FilterOption(name, id.toString()) } } },
            )
            "agency" -> {
                val countries = pages { offset, limit -> api.countries(limit, offset) }
                mapOf(
                    "country_code" to countries.mapNotNull { country ->
                        val name = country.name ?: return@mapNotNull null
                        val code = country.alpha_3_code ?: return@mapNotNull null
                        FilterOption(name, code)
                    },
                    "type__id" to pages { offset, limit -> api.agencyTypes(limit, offset) }
                        .mapNotNull { it.id?.let { id -> it.name?.let { name -> FilterOption(name, id.toString()) } } },
                )
            }
            "astronaut" -> {
                val countries = pages { offset, limit -> api.countries(limit, offset) }
                mapOf(
                    "nationality" to countries.mapNotNull { country ->
                        val nationality = country.nationality_name?.takeIf(String::isNotBlank)
                            ?: country.nationality_name_composed?.takeIf(String::isNotBlank)
                            ?: return@mapNotNull null
                        FilterOption(nationality, nationality)
                    }.distinctBy(FilterOption::value),
                    "agency_ids" to optionsFromAgencies(),
                    "status_ids" to pages { offset, limit -> api.astronautStatuses(limit, offset) }
                        .mapNotNull { it.id?.let { id -> it.name?.let { name -> FilterOption(name, id.toString()) } } },
                )
            }
            else -> emptyMap()
        }.mapValues { (_, values) -> values.sortedBy(FilterOption::label) }
    }
    override suspend fun getUpcomingLaunches(search: String?, agencyId: Int?, year: Int?, statusId: Int?) = fetchAllPages { offset, limit ->
        api.upcomingLaunches(search = search, agencyId = agencyId, year = year, statusId = statusId, offset = offset, limit = limit)
    }.map { it.toDomain() }
    override suspend fun getLaunchDetail(id: String) = api.launch(id).toDomain()
    override suspend fun getUpcomingEvents(search: String?, agencyId: Int?, year: Int?, typeId: Int?) =
        fetchAllPages { offset, limit ->
            api.upcomingEvents(
                limit = limit,
                offset = offset,
                search = search,
                agencyId = agencyId,
                year = year,
                typeIds = typeId?.toString(),
                dateGte = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date()),
            )
        }.map { it.toDomain() }
    override suspend fun getEventDetail(id: Int) = api.event(id).toDomain()
    override suspend fun getExpeditions(search: String?, agencyId: Int?, startGte: String?, endLte: String?, stationId: Int?) =
        fetchAllPages { offset, limit ->
            api.expeditions(
                search = search, agencyId = agencyId, startGte = startGte, endLte = endLte,
                stationId = stationId, offset = offset, limit = limit,
            )
        }.distinctBy { it.id }.map { it.toDomain() }
    override suspend fun getExpeditionDetail(id: Int) = api.expedition(id).toDomain()
    override suspend fun getAgencies(search: String?, countryCode: String?, typeId: Int?): List<Agency> {
        return fetchAllPages { offset, limit ->
            api.agencies(limit = limit, offset = offset, search = search, countryCode = countryCode, typeId = typeId)
        }.map { it.toDomain() }
    }
    override suspend fun getAgencyDetail(id: Int) = api.agency(id).toDomain()
    override suspend fun getAstronauts(search: String?, nationality: String?, agencyId: Int?, statusId: Int?) = fetchAllPages { offset, limit ->
        api.astronauts(search = search, nationality = nationality, agencyId = agencyId, statusId = statusId, offset = offset, limit = limit)
    }.map { it.toDomain() }
    override suspend fun getAstronautDetail(id: Int) = api.astronaut(id).toDomain()

    private suspend fun <T> fetchAllPages(
        pageRequest: suspend (offset: Int, limit: Int) -> PaginatedResponse<T>,
    ): List<T> {
        val results = mutableListOf<T>()
        var offset = 0
        var totalCount: Int
        do {
            val page = pageRequest(offset, PAGE_SIZE)
            totalCount = page.count
            if (page.results.isEmpty()) break
            results += page.results
            offset += page.results.size
        } while (offset < totalCount)
        return results
    }

    private companion object {
        const val PAGE_SIZE = 100
    }
}
