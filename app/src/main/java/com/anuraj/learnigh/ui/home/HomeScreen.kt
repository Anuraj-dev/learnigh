package com.anuraj.learnigh.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anuraj.learnigh.data.local.SettingsDataStore
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.components.CourseCard
import com.anuraj.learnigh.ui.components.EmptyState
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
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(repository, settings))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(IndigoPrimary, VioletSecondary),
                                    ),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(23.dp),
                            )
                        }
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Learnigh",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = "Your learning HQ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                GradientHero {
                    Column {
                        Text(
                            text = "WELCOME BACK",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Keep your momentum, ${state.displayName.ifBlank { "Learner" }}.",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.LocalFireDepartment,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(20.dp),
                            )
                            Text(
                                text = "  ${state.streakDays}-day learning streak",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.82f),
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            StatPill(
                                label = "In progress",
                                value = state.inProgress.size.toString(),
                                accent = IndigoPrimary.copy(alpha = 0.9f),
                                modifier = Modifier.weight(1f),
                            )
                            StatPill(
                                label = "Due soon",
                                value = state.dueSoon.size.toString(),
                                accent = WarningAmber,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            StatPill(
                                label = "Completed",
                                value = state.completedCount.toString(),
                                accent = SuccessGreen,
                                modifier = Modifier.weight(1f),
                            )
                            StatPill(
                                label = "Total",
                                value = state.totalCount.toString(),
                                accent = Color(0xFFFF8DE8),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            if (state.dueSoon.isNotEmpty()) {
                item { SectionHeader(title = "Due soon") }
                items(state.dueSoon, key = { "due-${it.id}" }) { course ->
                    CourseCard(
                        course = course,
                        onClick = { onOpenCourse(course.id) },
                    )
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
                    EmptyState(
                        icon = Icons.Rounded.RocketLaunch,
                        title = "Ready for your next chapter",
                        subtitle = "Add a course, playlist, or free resource and give it a little momentum.",
                        actionLabel = "Add your first course",
                        onAction = onAddCourse,
                    )
                }
            } else {
                items(state.inProgress, key = { "in-progress-${it.id}" }) { course ->
                    CourseCard(
                        course = course,
                        onClick = { onOpenCourse(course.id) },
                    )
                }
            }
        }
    }
}
