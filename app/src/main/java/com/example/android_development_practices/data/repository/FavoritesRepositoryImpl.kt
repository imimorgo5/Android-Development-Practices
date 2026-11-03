package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.local.FavoriteDao
import com.example.android_development_practices.domain.model.Favorite
import com.example.android_development_practices.domain.model.FavoriteSource
import com.example.android_development_practices.domain.repository.FavoritesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class FavoritesRepositoryImpl @Inject constructor(private val dao: FavoriteDao) : FavoritesRepository {
    private val toggleMutex = Mutex()

    override fun observeAll() = dao.observeAll().map { items ->
        items.map { Favorite(it.key, it.category, it.title, it.subtitle, it.imageUrl, it.detailId) }
    }

    override suspend fun toggle(item: FavoriteSource) {
        toggleMutex.withLock {
            val snapshot = favoriteSnapshot(item)
            if (dao.contains(snapshot.key)) dao.remove(snapshot.key) else dao.add(snapshot)
        }
    }

    override suspend fun remove(key: String) = dao.remove(key)
}
