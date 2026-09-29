package com.example.android_development_practices.ui.home

import androidx.lifecycle.ViewModel
import com.example.android_development_practices.R
import com.example.android_development_practices.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class HomeCategory(
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val route: String,
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    val categories: List<HomeCategory> = listOf(
        HomeCategory(
            title = "Ближайшие запуски",
            subtitle = "Предстоящие старты ракет",
            imageRes = R.drawable.launches,
            route = Routes.LAUNCH_LIST,
        ),
        HomeCategory(
            title = "Ближайшие события",
            subtitle = "Значимые события в космической отрасли",
            imageRes = R.drawable.events,
            route = Routes.EVENT_LIST,
        ),
        HomeCategory(
            title = "Экспедиции",
            subtitle = "Долговременные экспедиции на орбитальные станции",
            imageRes = R.drawable.expeditions,
            route = Routes.EXPEDITION_LIST,
        ),
        HomeCategory(
            title = "Космические агентства",
            subtitle = "Организации, отвечающие за космические программы",
            imageRes = R.drawable.agencies,
            route = Routes.AGENCY_LIST,
        ),
        HomeCategory(
            title = "Космонавты и астронавты",
            subtitle = "Люди, побывавшие в космосе",
            imageRes = R.drawable.astronauts,
            route = Routes.ASTRONAUT_LIST,
        ),
    )
}