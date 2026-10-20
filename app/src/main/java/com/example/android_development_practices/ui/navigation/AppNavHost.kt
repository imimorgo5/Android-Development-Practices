package com.example.android_development_practices.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.android_development_practices.data.local.FavoriteEntity
import com.example.android_development_practices.ui.common.FavoriteViewModel
import com.example.android_development_practices.ui.common.ListItemCard
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

private fun BottomTab.icon(): ImageVector = when (this) {
    BottomTab.Home -> Icons.Filled.Home
    BottomTab.Favorites -> Icons.Filled.Favorite
}

@Composable
fun MainApp() {
    val tabNavController = rememberNavController()
    val homeNavController = rememberNavController()
    var returnToFavorites by remember { mutableStateOf(false) }

    fun handleDetailBack() {
        homeNavController.popBackStack()
        if (returnToFavorites) {
            returnToFavorites = false
            tabNavController.navigate(BottomTab.Favorites.route) { launchSingleTop = true }
        }
    }

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
                HomeTabNavHost(homeNavController, returnToFavorites, ::handleDetailBack)
            }
            composable(BottomTab.Favorites.route) {
                FavoritesRoute(
                    onOpen = { item ->
                        returnToFavorites = true
                        tabNavController.navigate(BottomTab.Home.route)
                        homeNavController.navigate(when (item.category) {
                            "launch" -> Routes.launchDetail(item.detailId)
                            "event" -> Routes.eventDetail(item.detailId.toInt())
                            "expedition" -> Routes.expeditionDetail(item.detailId.toInt())
                            "agency" -> Routes.agencyDetail(item.detailId.toInt())
                            else -> Routes.astronautDetail(item.detailId.toInt())
                        })
                    },
                )
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
private fun HomeTabNavHost(
    homeNavController: NavHostController,
    returnToFavorites: Boolean,
    onDetailBack: () -> Unit,
) {
    val backStack by homeNavController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val detailRoutes = setOf(
        Routes.LAUNCH_DETAIL, Routes.EVENT_DETAIL, Routes.EXPEDITION_DETAIL,
        Routes.AGENCY_DETAIL, Routes.ASTRONAUT_DETAIL,
    )
    BackHandler(enabled = returnToFavorites && currentRoute in detailRoutes) { onDetailBack() }
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
                onBack = onDetailBack,
            )
        }
        composable(Routes.EVENT_DETAIL, arguments = idArgument()) { entry ->
            EventDetailRoute(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = onDetailBack,
            )
        }
        composable(Routes.EXPEDITION_DETAIL, arguments = idArgument()) { entry ->
            ExpeditionDetailRoute(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = onDetailBack,
            )
        }
        composable(Routes.AGENCY_DETAIL, arguments = idArgument()) { entry ->
            AgencyDetailRoute(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = onDetailBack,
            )
        }
        composable(Routes.ASTRONAUT_DETAIL, arguments = idArgument()) { entry ->
            AstronautDetailRoute(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = onDetailBack,
            )
        }
    }
}

private fun idArgument() = listOf(navArgument("id") { type = NavType.StringType })

@Composable
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
private fun FavoritesRoute(onOpen: (FavoriteEntity) -> Unit) {
    val vm: FavoriteViewModel = hiltViewModel()
    val items by vm.favorites.collectAsState()
    androidx.compose.material3.Scaffold(topBar = { androidx.compose.material3.TopAppBar(title = { Text("Избранное") }) }) { pad ->
        if (items.isEmpty()) Text("Здесь будут сохранённые записи", Modifier.fillMaxSize().padding(pad).wrapContentSize())
        else LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items, key = { it.key }) { item ->
                ListItemCard(
                    title = item.title,
                    subtitle = "${item.category.replaceFirstChar { it.uppercase() }} · ${item.subtitle.ifBlank { "—" }}",
                    thumbnailUrl = item.imageUrl,
                    thumbnailFallback = item.category.uppercase(),
                    onClick = { onOpen(item) },
                    isFavorite = true,
                    onFavoriteClick = { vm.remove(item.key) },
                )
            }
        }
    }
}
