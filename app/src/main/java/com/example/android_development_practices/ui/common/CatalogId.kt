package com.example.android_development_practices.ui.common

// Keep the original preference keys so existing filters and cached options are restored.
// These keys are storage identifiers and must not change when screen titles change.
enum class CatalogId(val apiKey: String, val settingsKey: String) {
    LAUNCH("launch", "Ближайшие запуски"),
    EVENT("event", "Ближайшие события"),
    EXPEDITION("expedition", "Экспедиции"),
    AGENCY("agency", "Космические агентства"),
    ASTRONAUT("astronaut", "Космонавты и астронавты"),
}
