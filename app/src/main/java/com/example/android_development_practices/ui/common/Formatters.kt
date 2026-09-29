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
    val datePart = iso.substringBefore('T')
    val timePart = iso.substringAfter('T', missingDelimiterValue = "")
    val parts = buildList {
        Regex("(\\d+)([YMWD])").findAll(datePart).forEach { match ->
            val unit = when (match.groupValues[2]) {
                "Y" -> "г."
                "M" -> "мес."
                "W" -> "нед."
                else -> "дн."
            }
            add("${match.groupValues[1]} $unit")
        }
        Regex("(\\d+)([HMS])").findAll(timePart).forEach { match ->
            val unit = when (match.groupValues[2]) {
                "H" -> "ч"
                "M" -> "мин"
                else -> "сек"
            }
            add("${match.groupValues[1]} $unit")
        }
    }
    return parts.joinToString(" ").ifBlank { iso }
}
