package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.local.FavoriteDetailsCache
import com.example.android_development_practices.data.model.dto.Launch as LaunchDto
import com.example.android_development_practices.data.remote.SpaceApi
import com.example.android_development_practices.domain.FavoriteUseCases
import com.example.android_development_practices.domain.SpaceUseCases
import com.example.android_development_practices.launchFixture
import java.lang.reflect.Proxy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class SpaceRepositoryDetailsTest {
    @Test
    fun detailsRemainAvailableOfflineThroughUseCase() = runBlocking {
        val dao = InMemoryFavoriteDao()
        val launch = launchFixture().copy(missionDescription = "Offline mission")
        FavoriteUseCases(FavoritesRepositoryImpl(dao)).toggle(launch)
        val useCases = SpaceUseCases(SpaceRepositoryImpl(
            launchApi { error("Offline") },
            FavoriteDetailsCache(dao),
        ))

        assertEquals(launch, useCases.launch(launch.id))
    }

    @Test
    fun successfulRequestRefreshesFavoriteForTheNextOfflineRequest() = runBlocking {
        val dao = InMemoryFavoriteDao()
        val launch = launchFixture()
        FavoriteUseCases(FavoritesRepositoryImpl(dao)).toggle(launch)
        var offline = false
        val useCases = SpaceUseCases(SpaceRepositoryImpl(
            launchApi {
                check(!offline) { "Offline" }
                LaunchDto(id = launch.id, name = "Updated launch", net = "2026-11-03T12:00:00Z")
            },
            FavoriteDetailsCache(dao),
        ))

        val fresh = useCases.launch(launch.id)
        offline = true

        assertEquals("Updated launch", fresh.name)
        assertEquals(fresh, useCases.launch(launch.id))
    }

    @Test
    fun failureWithoutFavoriteIsPropagated() = runBlocking {
        val failure = IllegalStateException("Offline")
        val useCases = SpaceUseCases(SpaceRepositoryImpl(
            launchApi { throw failure },
            FavoriteDetailsCache(InMemoryFavoriteDao()),
        ))

        try {
            useCases.launch("unknown")
            fail("Expected request failure")
        } catch (error: IllegalStateException) {
            assertSame(failure, error)
        }
    }

    @Test
    fun cancellationIsPropagatedEvenWhenFavoriteExists() = runBlocking {
        val dao = InMemoryFavoriteDao()
        val launch = launchFixture()
        FavoriteUseCases(FavoritesRepositoryImpl(dao)).toggle(launch)
        val cancellation = CancellationException("Cancelled")
        val useCases = SpaceUseCases(SpaceRepositoryImpl(
            launchApi { throw cancellation },
            FavoriteDetailsCache(dao),
        ))

        try {
            useCases.launch(launch.id)
            fail("Expected cancellation")
        } catch (error: CancellationException) {
            assertSame(cancellation, error)
        }
    }

    private fun launchApi(request: () -> LaunchDto): SpaceApi = Proxy.newProxyInstance(
        SpaceApi::class.java.classLoader,
        arrayOf(SpaceApi::class.java),
    ) { _, method, _ ->
        check(method.name == "launch") { "Unexpected API call: ${method.name}" }
        request()
    } as SpaceApi
}
