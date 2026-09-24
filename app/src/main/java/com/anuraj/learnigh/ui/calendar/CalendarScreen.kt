package com.anuraj.learnigh.ui.calendar

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.components.CourseCard
import com.anuraj.learnigh.ui.components.dueColor
import com.anuraj.learnigh.ui.theme.IndigoPrimary
import com.anuraj.learnigh.util.DateUtils
import com.anuraj.learnigh.viewmodel.CalendarViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    repository: CourseRepository,
    onOpenCourse: (Long) -> Unit,
) {
    val vm: CalendarViewModel = viewModel(factory = CalendarViewModel.factory(repository))
    val deadlines by vm.deadlines.collectAsStateWithLifecycle()

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
                Text(
                    "No upcoming deadlines.\nAdd one when editing a course.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
            var lastMonth: String? = null
            deadlines.forEach { course ->
                val month = DateUtils.monthYear(course.deadline!!)
                if (month != lastMonth) {
                    item(key = "m-$month") {
                        Text(
                            month,
                            style = MaterialTheme.typography.titleLarge,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                    }
                    lastMonth = month
                }
                item(key = course.id) {
                    Row(verticalAlignment = Alignment.Top) {
                        DeadlineBadge(deadline = course.deadline!!)
                        Spacer(Modifier.width(12.dp))
                        CourseCard(
                            course = course,
                            onClick = { onOpenCourse(course.id) },
                            modifier = Modifier.weight(1f),
                        )
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
