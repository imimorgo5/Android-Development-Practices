package com.example.android_development_practices.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class Agency(
    val id: Int = 0,
    val url: String? = null,
    val name: String = "",
    val abbrev: String? = null,
    val type: AgencyType? = null,
    val featured: Boolean? = null,
    val country: List<GeopoliticalCountry> = emptyList(),
    val description: String? = null,
    val administrator: String? = null,
    val founding_year: Int? = null,
    val info_url: String? = null,
    val wiki_url: String? = null,
    val image: ApiImage? = null,
    val logo: ApiImage? = null,
    val total_launch_count: Int? = null,
    val successful_launches: Int? = null,
    val failed_launches: Int? = null,
    val pending_launches: Int? = null,
    val successful_landings: Int? = null,
    val attempted_landings: Int? = null,
)