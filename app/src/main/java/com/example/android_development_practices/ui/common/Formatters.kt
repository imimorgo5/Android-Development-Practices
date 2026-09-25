package com.example.android_development_practices.ui.common

fun formatIsoDate(iso: String?, fallback: String = ""): String {
    if (iso.isNullOrBlank()) return fallback
    val datePart = iso.substringBefore('T')
    val parts = datePart.split("-")
    return if (parts.size == 3) {
        "${parts[2]}.${parts[1]}.${parts[0]}"
    } else {
        datePart
    }
}

fun formatIsoDuration(iso: String?, fallback: String = "—"): String {
    if (iso.isNullOrBlank()) return fallback
    val days = Regex("(\\d+)D").find(iso)?.groupValues?.get(1)
    val hours = Regex("(\\d+)H").find(iso)?.groupValues?.get(1)
    val minutes = Regex("(\\d+)M").find(iso)?.groupValues?.get(1)

    val parts = buildList {
        days?.let { add("$it дн.") }
        hours?.let { add("$it ч") }
        minutes?.let { add("$it мин") }
    }
    return parts.joinToString(" ").ifBlank { iso }
}