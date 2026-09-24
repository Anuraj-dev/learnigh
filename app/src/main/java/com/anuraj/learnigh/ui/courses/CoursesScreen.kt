package com.anuraj.learnigh.ui.courses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.components.CourseCard
import com.anuraj.learnigh.ui.components.EmptyState
import com.anuraj.learnigh.viewmodel.CoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    repository: CourseRepository,
    onOpenCourse: (Long) -> Unit,
    onAddCourse: () -> Unit,
) {
    val viewModel: CoursesViewModel = viewModel(factory = CoursesViewModel.factory(repository))
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val filters by viewModel.filterState.collectAsStateWithLifecycle()
    val filtersActive = filters.query.isNotBlank() || filters.status != null || filters.sourceType != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Courses")
                        Text(
                            text = "${courses.size} ${if (courses.size == 1) "course" else "courses"} in this view",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            OutlinedTextField(
                value = filters.query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                placeholder = { Text("Search title, provider, or tags") },
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                },
                trailingIcon = if (filters.query.isNotEmpty()) {
                    {
                        IconButton(onClick = { viewModel.setQuery("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                        }
                    }
                } else {
                    null
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column(modifier = Modifier.padding(vertical = 14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            text = "FILTER LIBRARY",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        if (filtersActive) {
                            TextButton(onClick = viewModel::clearFilters) {
                                Text("Clear all")
                            }
                        }
                    }
                    FilterLabel("Status")
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = filters.status == null,
                            onClick = { viewModel.setStatus(null) },
                            label = { Text("All") },
                            colors = premiumChipColors(),
                        )
                        CourseStatus.entries.forEach { status ->
                            FilterChip(
                                selected = filters.status == status,
                                onClick = {
                                    viewModel.setStatus(
                                        if (filters.status == status) null else status,
                                    )
                                },
                                label = { Text(status.label) },
                                colors = premiumChipColors(),
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    FilterLabel("Source")
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = filters.sourceType == null,
                            onClick = { viewModel.setSourceType(null) },
                            label = { Text("All") },
                            colors = premiumChipColors(),
                        )
                        SourceType.entries.forEach { sourceType ->
                            FilterChip(
                                selected = filters.sourceType == sourceType,
                                onClick = {
                                    viewModel.setSourceType(
                                        if (filters.sourceType == sourceType) null else sourceType,
                                    )
                                },
                                label = { Text(sourceType.label) },
                                colors = premiumChipColors(),
                            )
                        }
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 112.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                if (courses.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Rounded.SearchOff,
                            title = if (filtersActive) "No courses match" else "Your library is ready",
                            subtitle = if (filtersActive) {
                                "Try a broader search or reset the filters to see more learning."
                            } else {
                                "Add the course you bought, a playlist you follow, or a free resource worth finishing."
                            },
                            actionLabel = if (filtersActive) "Clear filters" else "Add a course",
                            onAction = if (filtersActive) viewModel::clearFilters else onAddCourse,
                        )
                    }
                }
                items(courses, key = { it.id }) { course ->
                    CourseCard(
                        course = course,
                        onClick = { onOpenCourse(course.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 16.dp, bottom = 7.dp),
    )
}

@Composable
private fun premiumChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = MaterialTheme.colorScheme.surface,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
)
