package com.example.android_development_practices.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android_development_practices.ui.agency.AgencyDetailRoute
import com.example.android_development_practices.ui.agency.AgencyListRoute
import com.example.android_development_practices.ui.astronaut.AstronautDetailRoute
import com.example.android_development_practices.ui.astronaut.AstronautListRoute
import com.example.android_development_practices.ui.event.EventDetailRoute
import com.example.android_development_practices.ui.event.EventListRoute
import com.example.android_development_practices.ui.expedition.ExpeditionDetailRoute
import com.example.android_development_practices.ui.expedition.ExpeditionListRoute
import com.example.android_development_practices.ui.home.HomeRoute
import com.example.android_development_practices.ui.launch.LaunchDetailRoute
import com.example.android_development_practices.ui.launch.LaunchListRoute

private fun BottomTab.icon(): ImageVector = when (this) {
    BottomTab.Home -> Icons.Filled.Home
    BottomTab.Settings -> Icons.Filled.Settings
}

@Composable
fun MainApp() {
    val tabNavController = rememberNavController()
    val homeNavController = rememberNavController()

    val tabBackStack by tabNavController.currentBackStackEntryAsState()
    val currentRoute = tabBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            selectTab(tabNavController, homeNavController, tab, currentRoute)
                        },
                        icon = { Icon(imageVector = tab.icon(), contentDescription = tab.label) },
                        label = { Text(text = tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = BottomTab.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(BottomTab.Home.route) {
                HomeTabNavHost(homeNavController)
            }
            composable(BottomTab.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

private fun selectTab(
    tabNavController: NavHostController,
    homeNavController: NavHostController,
    tab: BottomTab,
    currentRoute: String?,
) {
    if (currentRoute != tab.route) {
        tabNavController.navigate(tab.route) {
            popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    } else {
        homeNavController.popBackStack(Routes.HOME, inclusive = false)
    }
}

@Composable
private fun HomeTabNavHost(homeNavController: NavHostController) {
    NavHost(
        navController = homeNavController,
        startDestination = Routes.HOME,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(Routes.HOME) {
            HomeRoute(onCategoryClick = { route -> homeNavController.navigate(route) })
        }

        composable(Routes.LAUNCH_LIST) {
            LaunchListRoute(
                onBack = { homeNavController.popBackStack() },
                onLaunchClick = { homeNavController.navigate(Routes.launchDetail(it)) },
            )
        }
        composable(Routes.EVENT_LIST) {
            EventListRoute(
                onBack = { homeNavController.popBackStack() },
                onEventClick = { homeNavController.navigate(Routes.eventDetail(it)) },
            )
        }
        composable(Routes.EXPEDITION_LIST) {
            ExpeditionListRoute(
                onBack = { homeNavController.popBackStack() },
                onExpeditionClick = { homeNavController.navigate(Routes.expeditionDetail(it)) },
            )
        }
        composable(Routes.AGENCY_LIST) {
            AgencyListRoute(
                onBack = { homeNavController.popBackStack() },
                onAgencyClick = { homeNavController.navigate(Routes.agencyDetail(it)) },
            )
        }
        composable(Routes.ASTRONAUT_LIST) {
            AstronautListRoute(
                onBack = { homeNavController.popBackStack() },
                onAstronautClick = { homeNavController.navigate(Routes.astronautDetail(it)) },
            )
        }

        composable(Routes.LAUNCH_DETAIL, arguments = idArgument()) { entry ->
            LaunchDetailRoute(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = { homeNavController.popBackStack() },
            )
        }
        composable(Routes.EVENT_DETAIL, arguments = idArgument()) { entry ->
            EventDetailRoute(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = { homeNavController.popBackStack() },
            )
        }
        composable(Routes.EXPEDITION_DETAIL, arguments = idArgument()) { entry ->
            ExpeditionDetailRoute(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = { homeNavController.popBackStack() },
            )
        }
        composable(Routes.AGENCY_DETAIL, arguments = idArgument()) { entry ->
            AgencyDetailRoute(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = { homeNavController.popBackStack() },
            )
        }
        composable(Routes.ASTRONAUT_DETAIL, arguments = idArgument()) { entry ->
            AstronautDetailRoute(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = { homeNavController.popBackStack() },
            )
        }
    }
}

private fun idArgument() = listOf(navArgument("id") { type = NavType.StringType })

@Composable
private fun SettingsScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Здесь появятся настройки приложения",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}