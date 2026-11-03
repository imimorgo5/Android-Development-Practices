package com.example.android_development_practices.domain.repository

import com.example.android_development_practices.domain.model.Favorite
import com.example.android_development_practices.domain.model.FavoriteSource
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeAll(): Flow<List<Favorite>>
    suspend fun toggle(item: FavoriteSource)
    suspend fun remove(key: String)
}
