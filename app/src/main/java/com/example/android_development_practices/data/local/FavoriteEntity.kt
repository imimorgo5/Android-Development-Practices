package com.example.android_development_practices.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Compact offline snapshot shared by all five catalog categories. */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val key: String,
    val category: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String?,
    val detailId: String,
    val detailPayload: String? = null,
)
