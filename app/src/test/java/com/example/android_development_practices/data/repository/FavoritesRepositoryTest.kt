package com.example.android_development_practices.data.repository

import com.example.android_development_practices.domain.FavoriteUseCases
import com.example.android_development_practices.domain.model.Launch
import com.example.android_development_practices.domain.model.favoriteKey
import com.example.android_development_practices.launchFixture
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoritesRepositoryTest {
    @Test
    fun toggleStoresAnOfflineSnapshotAndExposesDomainFavorites() = runBlocking {
        val dao = InMemoryFavoriteDao()
        val useCases = FavoriteUseCases(FavoritesRepositoryImpl(dao))
        val launch = launchFixture().copy(providerName = "SpaceX", missionDescription = "Mission details")

        useCases.toggle(launch)

        val favorite = useCases.observeAll().first().single()
        assertEquals(launch.favoriteKey, favorite.key)
        assertEquals("SpaceX", favorite.subtitle)
        assertEquals(launch.id, favorite.detailId)
        val payload = checkNotNull(dao.get(favorite.key)?.detailPayload)
        assertEquals(launch, Json.decodeFromString(Launch.serializer(), payload))

        useCases.toggle(launch)
        assertTrue(useCases.observeAll().first().isEmpty())
    }

    @Test
    fun removeKeepsOtherFavorites() = runBlocking {
        val useCases = FavoriteUseCases(FavoritesRepositoryImpl(InMemoryFavoriteDao()))
        val first = launchFixture()
        val second = first.copy(id = "launch-2")
        useCases.toggle(first)
        useCases.toggle(second)

        useCases.remove(first.favoriteKey)

        assertEquals(listOf(second.favoriteKey), useCases.observeAll().first().map { it.key })
    }

}
