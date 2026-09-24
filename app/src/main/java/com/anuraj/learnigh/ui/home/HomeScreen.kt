package com.anuraj.learnigh.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anuraj.learnigh.data.local.SettingsDataStore
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.components.CourseCard
import com.anuraj.learnigh.ui.components.GradientHero
import com.anuraj.learnigh.ui.components.SectionHeader
import com.anuraj.learnigh.ui.components.StatPill
import com.anuraj.learnigh.ui.theme.IndigoPrimary
import com.anuraj.learnigh.ui.theme.SuccessGreen
import com.anuraj.learnigh.ui.theme.VioletSecondary
import com.anuraj.learnigh.ui.theme.WarningAmber
import com.anuraj.learnigh.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: CourseRepository,
    settings: SettingsDataStore,
    onOpenCourse: (Long) -> Unit,
    onAddCourse: () -> Unit,
    onSeeAllCourses: () -> Unit,
) {
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.factory(repository, settings))
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Learnigh", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Your learning HQ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCourse,
                containerColor = IndigoPrimary,
                contentColor = Color.White,
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add course")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                GradientHero {
                    Column {
                        Text(
                            "Hey, ${state.displayName}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.LocalFireDepartment,
                                contentDescription = null,
                                tint = WarningAmber,
                            )
                            Text(
                                " ${state.streakDays}-day streak",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(0.85f),
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            StatPill("In progress", "${state.inProgress.size}", IndigoPrimary)
                            StatPill("Due soon", "${state.dueSoon.size}", WarningAmber)
                            StatPill("Done", "${state.completedCount}", SuccessGreen)
                            StatPill("Total", "${state.totalCount}", VioletSecondary)
                        }
                    }
                }
            }

            if (state.dueSoon.isNotEmpty()) {
                item { SectionHeader("Due soon") }
                items(state.dueSoon, key = { "due-${it.id}" }) { course ->
                    CourseCard(course = course, onClick = { onOpenCourse(course.id) })
                }
            }

            item {
                SectionHeader(
                    title = "In progress",
                    action = "See all",
                    onAction = onSeeAllCourses,
                )
            }
            if (state.inProgress.isEmpty()) {
                item {
                    Text(
                        "Nothing in progress — tap + to add a course.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            } else {
                items(state.inProgress, key = { "ip-${it.id}" }) { course ->
                    CourseCard(course = course, onClick = { onOpenCourse(course.id) })
                }
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}
