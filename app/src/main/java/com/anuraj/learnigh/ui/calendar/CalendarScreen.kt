package com.anuraj.learnigh.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.components.CourseCard
import com.anuraj.learnigh.ui.components.dueColor
import com.anuraj.learnigh.ui.theme.IndigoPrimary
import com.anuraj.learnigh.util.DateUtils
import com.anuraj.learnigh.viewmodel.CalendarViewModel
import java.util.Calendar

private sealed class DeadlineRow {
    data class MonthHeader(val label: String) : DeadlineRow()
    data class CourseRow(val course: CourseEntity) : DeadlineRow()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    repository: CourseRepository,
    onOpenCourse: (Long) -> Unit,
) {
    val vm: CalendarViewModel = viewModel(factory = CalendarViewModel.factory(repository))
    val deadlines by vm.deadlines.collectAsStateWithLifecycle()

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
                add(DeadlineRow.CourseRow(course))
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
                            "Sorted by due date",
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
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (deadlines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📅", style = MaterialTheme.typography.displaySmall)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No upcoming deadlines.\nAdd one when editing a course.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = rows,
                key = { row ->
                    when (row) {
                        is DeadlineRow.MonthHeader -> "m-${row.label}"
                        is DeadlineRow.CourseRow -> "c-${row.course.id}"
                    }
                },
            ) { row ->
                when (row) {
                    is DeadlineRow.MonthHeader -> {
                        Text(
                            row.label,
                            style = MaterialTheme.typography.titleLarge,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                    }
                    is DeadlineRow.CourseRow -> {
                        Row(verticalAlignment = Alignment.Top) {
                            DeadlineBadge(deadline = row.course.deadline!!)
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
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun DeadlineBadge(deadline: Long) {
    val cal = Calendar.getInstance().apply { timeInMillis = deadline }
    val day = cal.get(Calendar.DAY_OF_MONTH).toString()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 10.dp),
    ) {
        Text(
            day,
            style = MaterialTheme.typography.titleLarge,
            color = dueColor(deadline),
        )
        Text(
            cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault())
                ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
