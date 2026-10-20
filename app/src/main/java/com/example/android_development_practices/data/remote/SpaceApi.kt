package com.example.android_development_practices.data.remote

import com.example.android_development_practices.data.model.dto.Agency
import com.example.android_development_practices.data.model.dto.Astronaut
import com.example.android_development_practices.data.model.dto.Expedition
import com.example.android_development_practices.data.model.dto.Launch
import com.example.android_development_practices.data.model.dto.PaginatedResponse
import com.example.android_development_practices.data.model.dto.SpaceEvent
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceApi {
    @GET("2.3.0/launches/upcoming/")
    suspend fun upcomingLaunches(
        @Query("limit") limit: Int = 20,
        @Query("mode") mode: String = "normal",
    ): PaginatedResponse<Launch>

    @GET("2.3.0/launches/{id}/")
    suspend fun launch(
        @Path("id") id: String,
        @Query("mode") mode: String = "detailed",
    ): Launch

    @GET("2.3.0/events/upcoming/")
    suspend fun upcomingEvents(
        @Query("limit") limit: Int = 20,
        @Query("mode") mode: String = "normal",
    ): PaginatedResponse<SpaceEvent>

    @GET("2.3.0/events/{id}/")
    suspend fun event(
        @Path("id") id: Int,
        @Query("mode") mode: String = "detailed",
    ): SpaceEvent

    @GET("2.3.0/expeditions/")
    suspend fun expeditions(
        @Query("limit") limit: Int = 20,
        @Query("mode") mode: String = "normal",
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
        @Query("ordering") ordering: String = "name",
        @Query("mode") mode: String = "normal",
    ): PaginatedResponse<Agency>

    @GET("2.3.0/agencies/{id}/")
    suspend fun agency(
        @Path("id") id: Int,
        @Query("mode") mode: String = "detailed",
    ): Agency

    @GET("2.3.0/astronauts/")
    suspend fun astronauts(
        @Query("limit") limit: Int = 50,
        @Query("mode") mode: String = "normal",
    ): PaginatedResponse<Astronaut>

    @GET("2.3.0/astronauts/{id}/")
    suspend fun astronaut(
        @Path("id") id: Int,
        @Query("mode") mode: String = "detailed",
    ): Astronaut
}
