package com.example.android_development_practices.data.remote

import com.example.android_development_practices.data.model.dto.Agency
import com.example.android_development_practices.data.model.dto.Astronaut
import com.example.android_development_practices.data.model.dto.Expedition
import com.example.android_development_practices.data.model.dto.Launch
import com.example.android_development_practices.data.model.dto.PaginatedResponse
import com.example.android_development_practices.data.model.dto.SpaceEvent
import com.example.android_development_practices.data.model.dto.LaunchStatus
import com.example.android_development_practices.data.model.dto.EventType
import com.example.android_development_practices.data.model.dto.AgencyType
import com.example.android_development_practices.data.model.dto.AstronautStatus
import com.example.android_development_practices.data.model.dto.GeopoliticalCountry
import com.example.android_development_practices.data.model.dto.SpaceStationNormal
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceApi {
    @GET("2.3.0/config/launch_statuses/")
    suspend fun launchStatuses(@Query("limit") limit: Int = 100, @Query("offset") offset: Int = 0): PaginatedResponse<LaunchStatus>

    @GET("2.3.0/config/event_types/")
    suspend fun eventTypes(@Query("limit") limit: Int = 100, @Query("offset") offset: Int = 0): PaginatedResponse<EventType>

    @GET("2.3.0/config/agency_types/")
    suspend fun agencyTypes(@Query("limit") limit: Int = 100, @Query("offset") offset: Int = 0): PaginatedResponse<AgencyType>

    @GET("2.3.0/config/astronaut_statuses/")
    suspend fun astronautStatuses(@Query("limit") limit: Int = 100, @Query("offset") offset: Int = 0): PaginatedResponse<AstronautStatus>

    @GET("2.3.0/config/countries/")
    suspend fun countries(@Query("limit") limit: Int = 100, @Query("offset") offset: Int = 0): PaginatedResponse<GeopoliticalCountry>

    @GET("2.3.0/space_stations/")
    suspend fun spaceStations(@Query("limit") limit: Int = 100, @Query("offset") offset: Int = 0, @Query("mode") mode: String = "normal"): PaginatedResponse<SpaceStationNormal>

    @GET("2.3.0/launches/upcoming/")
    suspend fun upcomingLaunches(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("mode") mode: String = "normal",
        @Query("search") search: String? = null,
        @Query("lsp__id") agencyId: Int? = null,
        @Query("year") year: Int? = null,
        @Query("status__ids") statusId: Int? = null,
        @Query("hide_recent_previous") hideRecentPrevious: Boolean = true,
        @Query("ordering") ordering: String = "-net",
    ): PaginatedResponse<Launch>

    @GET("2.3.0/launches/{id}/")
    suspend fun launch(
        @Path("id") id: String,
        @Query("mode") mode: String = "detailed",
    ): Launch

    @GET("2.3.0/events/upcoming/")
    suspend fun upcomingEvents(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("mode") mode: String = "normal",
        @Query("search") search: String? = null,
        @Query("agency__ids") agencyId: Int? = null,
        @Query("year") year: Int? = null,
        @Query("type__ids") typeIds: String? = null,
        @Query("date__gte") dateGte: String? = null,
        @Query("hide_recent_previous") hideRecentPrevious: Boolean = true,
        @Query("ordering") ordering: String = "-date",
    ): PaginatedResponse<SpaceEvent>

    @GET("2.3.0/events/{id}/")
    suspend fun event(
        @Path("id") id: Int,
        @Query("mode") mode: String = "detailed",
    ): SpaceEvent

    @GET("2.3.0/expeditions/")
    suspend fun expeditions(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("mode") mode: String = "normal",
        @Query("search") search: String? = null,
        @Query("crew__astronaut__agency") agencyId: Int? = null,
        @Query("start__gte") startGte: String? = null,
        @Query("end__lte") endLte: String? = null,
        @Query("space_station") stationId: Int? = null,
        @Query("ordering") ordering: String = "-start",
    ): PaginatedResponse<Expedition>

    @GET("2.3.0/expeditions/{id}/")
    suspend fun expedition(
        @Path("id") id: Int,
        @Query("mode") mode: String = "detailed",
    ): Expedition

    @GET("2.3.0/agencies/")
    suspend fun agencies(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("ordering") ordering: String = "-total_launch_count",
        @Query("mode") mode: String = "normal",
        @Query("search") search: String? = null,
        @Query("country_code") countryCode: String? = null,
        @Query("type__id") typeId: Int? = null,
    ): PaginatedResponse<Agency>

    @GET("2.3.0/agencies/{id}/")
    suspend fun agency(
        @Path("id") id: Int,
        @Query("mode") mode: String = "detailed",
    ): Agency

    @GET("2.3.0/astronauts/")
    suspend fun astronauts(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("mode") mode: String = "normal",
        @Query("search") search: String? = null,
        @Query("nationality") nationality: String? = null,
        @Query("agency_ids") agencyId: Int? = null,
        @Query("status_ids") statusId: Int? = null,
        @Query("ordering") ordering: String = "name",
    ): PaginatedResponse<Astronaut>

    @GET("2.3.0/astronauts/{id}/")
    suspend fun astronaut(
        @Path("id") id: Int,
        @Query("mode") mode: String = "detailed",
    ): Astronaut
}
