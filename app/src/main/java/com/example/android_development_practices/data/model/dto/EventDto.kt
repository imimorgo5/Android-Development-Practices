package com.example.android_development_practices.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SpaceEvent(
    val id: Int = 0,
    val url: String? = null,
    val name: String = "",
    val slug: String? = null,
    val date: String? = null,
    val date_precision: NetPrecision? = null,
    val type: EventType? = null,
    val description: String? = null,
    val location: String? = null,
    val duration: String? = null,
    val webcast_live: Boolean? = null,
    val image: ApiImage? = null,
    val last_updated: String? = null,
    val info_urls: List<VidUrl> = emptyList(),
    val vid_urls: List<VidUrl> = emptyList(),
    val launches: List<LaunchBasic> = emptyList(),
    val program: List<ProgramNormal> = emptyList(),
)

/** Ссылка на трансляцию/видео (поля эндпоинтов vid_urls и info_urls). */
@Serializable
data class VidUrl(
    val priority: Int? = null,
    val source: String? = null,
    val title: String? = null,
    val url: String? = null,
    val live: Boolean? = null,
)

/** Краткое представление запуска, встречающееся внутри события. */
@Serializable
data class LaunchBasic(
    val id: String = "",
    val url: String? = null,
    val name: String = "",
    val net: String? = null,
    val image: ApiImage? = null,
)