package com.anuraj.learnigh.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anuraj.learnigh.data.local.SettingsDataStore
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.calendar.CalendarScreen
import com.anuraj.learnigh.ui.components.AddCourseFab
import com.anuraj.learnigh.ui.courses.CoursesScreen
import com.anuraj.learnigh.ui.detail.CourseDetailScreen
import com.anuraj.learnigh.ui.edit.EditCourseScreen
import com.anuraj.learnigh.ui.home.HomeScreen
import com.anuraj.learnigh.ui.settings.SettingsScreen

private data class Tab(
    val route: String,
    val label: String,
    val selected: ImageVector,
    val unselected: ImageVector,
)

private val tabs = listOf(
    Tab(Routes.HOME, "Home", Icons.Rounded.Home, Icons.Outlined.Home),
    Tab(
        Routes.COURSES,
        "Courses",
        Icons.AutoMirrored.Rounded.MenuBook,
        Icons.AutoMirrored.Outlined.MenuBook,
    ),
    Tab(Routes.CALENDAR, "Deadlines", Icons.Rounded.CalendarMonth, Icons.Outlined.CalendarMonth),
    Tab(Routes.SETTINGS, "Settings", Icons.Rounded.Settings, Icons.Outlined.Settings),
)

@Composable
fun LearnighNavHost(
    repository: CourseRepository,
    settings: SettingsDataStore,
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = tabs.any { it.route == currentRoute }
    val showAddFab = currentRoute == Routes.HOME || currentRoute == Routes.COURSES

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (showAddFab) {
                AddCourseFab(
                    onClick = { navController.navigate(Routes.ADD) },
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                ) {
                    tabs.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selected else tab.unselected,
                                    contentDescription = tab.label,
                                )
                            },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(contentPadding),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    repository = repository,
                    settings = settings,
                    onOpenCourse = { navController.navigate(Routes.detail(it)) },
                    onAddCourse = { navController.navigate(Routes.ADD) },
                    onSeeAllCourses = {
                        navController.navigate(Routes.COURSES) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(Routes.COURSES) {
                CoursesScreen(
                    repository = repository,
                    onOpenCourse = { navController.navigate(Routes.detail(it)) },
                    onAddCourse = { navController.navigate(Routes.ADD) },
                )
            }
            composable(Routes.CALENDAR) {
                CalendarScreen(
                    repository = repository,
                    onOpenCourse = { navController.navigate(Routes.detail(it)) },
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(settings = settings)
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("courseId") { type = NavType.LongType }),
            ) { entry ->
                val id = entry.arguments?.getLong("courseId") ?: return@composable
                CourseDetailScreen(
                    courseId = id,
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.edit(it)) },
                    onDeleted = { navController.popBackStack() },
                )
            }
            composable(
                route = Routes.EDIT,
                arguments = listOf(
                    navArgument("courseId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                ),
            ) { entry ->
                val raw = entry.arguments?.getLong("courseId") ?: -1L
                val id = raw.takeIf { it > 0 }
                EditCourseScreen(
                    courseId = id,
                    repository = repository,
                    defaultReminders = settings.remindersDefault,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
        }
    }
}
