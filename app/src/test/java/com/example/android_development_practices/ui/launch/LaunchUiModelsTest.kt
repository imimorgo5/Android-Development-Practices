package com.example.android_development_practices.ui.launch

import com.example.android_development_practices.launchFixture
import org.junit.Assert.assertEquals
import org.junit.Test

class LaunchUiModelsTest {
    @Test
    fun listUsesFallbacksWhenProviderDateAndRocketAreMissing() {
        val model = launchFixture().toListUiModel()

        assertEquals("— · дата уточняется", model.subtitle)
        assertEquals("🚀", model.thumbnailFallback)
    }

    @Test
    fun listFormatsDateAndPrefersRocketOverProviderForThumbnail() {
        val launch = launchFixture().copy(providerName = "SpaceX", rocketFamily = "Falcon", net = "2026-11-03T12:00:00Z")
        val model = launch.toListUiModel()

        assertEquals("SpaceX · 03.11.2026", model.subtitle)
        assertEquals("Fal", model.thumbnailFallback)
        assertEquals("Spa", launch.copy(rocketFamily = null).toListUiModel().thumbnailFallback)
    }

    @Test
    fun detailProvidesDisplayValuesForMissingFields() {
        val model = launchFixture().toDetailUiModel()

        assertEquals("🚀", model.rocketFamily)
        assertEquals("Статус уточняется", model.status)
        assertEquals("Описание миссии уточняется.", model.description)
        listOf(model.launchDate, model.rocketLaunchCount, model.provider, model.rocket,
            model.mission, model.orbit, model.pad, model.window).forEach { assertEquals("—", it) }
    }

    @Test
    fun detailFormatsDatesCountAndLocationWithoutLosingFavoriteSource() {
        val launch = launchFixture().copy(
            net = "2026-11-03T12:00:00Z",
            windowStart = "2026-11-04T12:00:00Z",
            rocketLaunchCount = 0,
            padName = "LC-39A",
            padLocationName = "Kennedy Space Center",
        )
        val model = launch.toDetailUiModel()

        assertEquals("03.11.2026", model.launchDate)
        assertEquals("04.11.2026", model.window)
        assertEquals("0", model.rocketLaunchCount)
        assertEquals("LC-39A · Kennedy Space Center", model.pad)
        assertEquals(launch, model.source)
        assertEquals("LC-39A", launch.copy(padLocationName = null).toDetailUiModel().pad)
    }
}
