package com.example.android_development_practices.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class Astronaut(
    val id: Int = 0,
    val url: String? = null,
    val name: String = "",
    val status: AstronautStatus? = null,
    val type: AstronautType? = null,
    val agency: AgencyMini? = null,
    val image: ApiImage? = null,
    val in_space: Boolean? = null,
    val time_in_space: String? = null,
    val eva_time: String? = null,
    val age: Int? = null,
    val date_of_birth: String? = null,
    val date_of_death: String? = null,
    val nationality: List<GeopoliticalCountry> = emptyList(),
    val bio: String? = null,
    val wiki: String? = null,
    val first_flight: String? = null,
    val last_flight: String? = null,
)
