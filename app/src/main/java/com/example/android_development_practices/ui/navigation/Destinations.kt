package com.example.android_development_practices.ui.navigation

object Routes {
    const val HOME = "home"

    const val LAUNCH_LIST = "launch_list"
    const val EVENT_LIST = "event_list"
    const val EXPEDITION_LIST = "expedition_list"
    const val AGENCY_LIST = "agency_list"
    const val ASTRONAUT_LIST = "astronaut_list"

    const val LAUNCH_DETAIL = "launch/{id}"
    const val EVENT_DETAIL = "event/{id}"
    const val EXPEDITION_DETAIL = "expedition/{id}"
    const val AGENCY_DETAIL = "agency/{id}"
    const val ASTRONAUT_DETAIL = "astronaut/{id}"

    fun launchDetail(id: String) = "launch/$id"
    fun eventDetail(id: Int) = "event/$id"
    fun expeditionDetail(id: Int) = "expedition/$id"
    fun agencyDetail(id: Int) = "agency/$id"
    fun astronautDetail(id: Int) = "astronaut/$id"
}

enum class BottomTab(
    val route: String,
    val label: String,
) {
    Home(route = Routes.HOME, label = "Главная"),
    Settings(route = "settings", label = "Настройки"),
}