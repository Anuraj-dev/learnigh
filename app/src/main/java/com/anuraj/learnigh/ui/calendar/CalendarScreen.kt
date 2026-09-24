package com.anuraj.learnigh.ui.calendar

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.components.CourseCard
import com.anuraj.learnigh.ui.components.EmptyState
import com.anuraj.learnigh.ui.components.dueColor
import com.anuraj.learnigh.ui.theme.IndigoPrimary
import com.anuraj.learnigh.ui.theme.VioletSecondary
import com.anuraj.learnigh.util.DateUtils
import com.anuraj.learnigh.viewmodel.CalendarViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private sealed class DeadlineRow {
    data class MonthHeader(val label: String) : DeadlineRow()
    data class CourseRow(
        val course: CourseEntity,
        val deadline: Long,
    ) : DeadlineRow()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    repository: CourseRepository,
    onOpenCourse: (Long) -> Unit,
) {
    val viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.factory(repository))
    val deadlines by viewModel.deadlines.collectAsStateWithLifecycle()
    val rows = remember(deadlines) {
        buildList {
            var lastMonth: String? = null
            deadlines.forEach { course ->
                val deadline = course.deadline ?: return@forEach
                val month = DateUtils.monthYear(deadline)
                if (month != lastMonth) {
                    add(DeadlineRow.MonthHeader(month))
                    lastMonth = month
                }
                add(DeadlineRow.CourseRow(course = course, deadline = deadline))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Deadlines")
                        Text(
                            text = "A calmer view of what is next",
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
        if (rows.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                EmptyState(
                    icon = Icons.Outlined.EventAvailable,
                    title = "Your deadline map is clear",
                    subtitle = "Add a deadline while editing a course and it will appear here, grouped by month.",
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "deadline-summary") {
                DeadlineSummary(
                    count = deadlines.size,
                    nextDeadline = deadlines.firstOrNull()?.deadline,
                )
            }
            items(
                items = rows,
                key = { row ->
                    when (row) {
                        is DeadlineRow.MonthHeader -> "month-${row.label}"
                        is DeadlineRow.CourseRow -> "course-${row.course.id}"
                    }
                },
                contentType = { row ->
                    when (row) {
                        is DeadlineRow.MonthHeader -> "month-header"
                        is DeadlineRow.CourseRow -> "deadline-course"
                    }
                },
            ) { row ->
                when (row) {
                    is DeadlineRow.MonthHeader -> MonthHeader(label = row.label)
                    is DeadlineRow.CourseRow -> {
                        Row(verticalAlignment = Alignment.Top) {
                            DeadlineBadge(deadline = row.deadline)
                            Spacer(Modifier.width(12.dp))
                            CourseCard(
                                course = row.course,
                                onClick = { onOpenCourse(row.course.id) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeadlineSummary(
    count: Int,
    nextDeadline: Long?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            IndigoPrimary.copy(alpha = 0.24f),
                            VioletSecondary.copy(alpha = 0.16f),
                        ),
                    ),
                )
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = "$count active ${if (count == 1) "deadline" else "deadlines"}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (nextDeadline == null) {
                        "Nothing scheduled"
                    } else {
                        "Next: ${DateUtils.dueLabel(nextDeadline)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MonthHeader(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(IndigoPrimary),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 10.dp),
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant),
        )
    }
}

@Composable
private fun DeadlineBadge(deadline: Long) {
    val date = remember(deadline) {
        Instant.ofEpochMilli(deadline).atZone(ZoneId.systemDefault())
    }
    val dayFormatter = remember { DateTimeFormatter.ofPattern("d", Locale.getDefault()) }
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMM", Locale.getDefault()) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 11.dp),
    ) {
        Text(
            text = date.format(dayFormatter),
            style = MaterialTheme.typography.titleLarge,
            color = dueColor(deadline),
        )
        Text(
            text = date.format(monthFormatter),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
