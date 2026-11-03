package com.example.android_development_practices.domain.model

sealed interface FavoriteSource

val FavoriteSource.favoriteKey: String
    get() = when (this) {
        is Launch -> "launch:$id"
        is SpaceEvent -> "event:$id"
        is Expedition -> "expedition:$id"
        is Agency -> "agency:$id"
        is Astronaut -> "astronaut:$id"
    }

data class Favorite(
    val key: String,
    val category: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String?,
    val detailId: String,
)
