package com.anuraj.learnigh.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.theme.IndigoPrimary
import com.anuraj.learnigh.util.DateUtils
import com.anuraj.learnigh.viewmodel.EditCourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCourseScreen(
    courseId: Long?,
    repository: CourseRepository,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val vm: EditCourseViewModel = viewModel(
        factory = EditCourseViewModel.factory(courseId, repository),
    )
    val form by vm.form.collectAsStateWithLifecycle()
    var pickStart by remember { mutableStateOf(false) }
    var pickDeadline by remember { mutableStateOf(false) }

    LaunchedEffect(form.saved) {
        if (form.saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (courseId == null) "Add course" else "Edit course") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = form.title,
                onValueChange = { v -> vm.update { it.copy(title = v) } },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = form.provider,
                onValueChange = { v -> vm.update { it.copy(provider = v) } },
                label = { Text("Provider (Udemy, Coursera…)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Text("Source type", style = MaterialTheme.typography.labelLarge)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SourceType.entries.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { type ->
                            FilterChip(
                                selected = form.sourceType == type,
                                onClick = { vm.update { it.copy(sourceType = type) } },
                                label = { Text(type.label) },
                            )
                        }
                    }
                }
            }
            OutlinedTextField(
                value = form.url,
                onValueChange = { v -> vm.update { it.copy(url = v) } },
                label = { Text("URL / deep-link") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { pickStart = true }, modifier = Modifier.weight(1f)) {
                    Text("Start: ${DateUtils.formatDate(form.purchaseOrStartDate)}")
                }
                OutlinedButton(onClick = { pickDeadline = true }, modifier = Modifier.weight(1f)) {
                    Text("Deadline: ${DateUtils.formatDate(form.deadline)}")
                }
            }

            Text("Status", style = MaterialTheme.typography.labelLarge)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CourseStatus.entries.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { status ->
                            FilterChip(
                                selected = form.status == status,
                                onClick = { vm.update { it.copy(status = status) } },
                                label = { Text(status.label) },
                            )
                        }
                    }
                }
            }

            Text("Progress ${form.progressPercent}%", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = form.progressPercent.toFloat(),
                onValueChange = { v -> vm.update { it.copy(progressPercent = v.toInt()) } },
                valueRange = 0f..100f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = IndigoPrimary,
                    activeTrackColor = IndigoPrimary,
                ),
            )

            OutlinedTextField(
                value = form.tags,
                onValueChange = { v -> vm.update { it.copy(tags = v) } },
                label = { Text("Tags (comma-separated)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = form.notes,
                onValueChange = { v -> vm.update { it.copy(notes = v) } },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Reminder")
                Switch(
                    checked = form.reminderEnabled,
                    onCheckedChange = { v -> vm.update { it.copy(reminderEnabled = v) } },
                )
            }

            form.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = vm::save,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
            ) {
                Text("Save course")
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (pickStart) {
        val state = rememberDatePickerState(initialSelectedDateMillis = form.purchaseOrStartDate)
        DatePickerDialog(
            onDismissRequest = { pickStart = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.update { it.copy(purchaseOrStartDate = state.selectedDateMillis) }
                    pickStart = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.update { it.copy(purchaseOrStartDate = null) }
                    pickStart = false
                }) { Text("Clear") }
            },
        ) { DatePicker(state = state) }
    }
    if (pickDeadline) {
        val state = rememberDatePickerState(initialSelectedDateMillis = form.deadline)
        DatePickerDialog(
            onDismissRequest = { pickDeadline = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.update { it.copy(deadline = state.selectedDateMillis) }
                    pickDeadline = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.update { it.copy(deadline = null) }
                    pickDeadline = false
                }) { Text("Clear") }
            },
        ) { DatePicker(state = state) }
    }
}
