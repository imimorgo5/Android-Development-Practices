package com.example.android_development_practices.domain

import com.example.android_development_practices.domain.model.FavoriteSource
import com.example.android_development_practices.domain.repository.FavoritesRepository
import javax.inject.Inject

class FavoriteUseCases @Inject constructor(private val repository: FavoritesRepository) {
    fun observeAll() = repository.observeAll()
    suspend fun toggle(item: FavoriteSource) = repository.toggle(item)
    suspend fun remove(key: String) = repository.remove(key)
}
