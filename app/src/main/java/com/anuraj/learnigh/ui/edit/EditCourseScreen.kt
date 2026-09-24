package com.anuraj.learnigh.ui.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.components.EmptyState
import com.anuraj.learnigh.util.DateUtils
import com.anuraj.learnigh.viewmodel.EditCourseViewModel
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditCourseScreen(
    courseId: Long?,
    repository: CourseRepository,
    defaultReminders: Flow<Boolean>,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val viewModel: EditCourseViewModel = viewModel(
        factory = EditCourseViewModel.factory(
            id = courseId,
            repo = repository,
            remindersDefault = defaultReminders,
        ),
    )
    val form by viewModel.form.collectAsStateWithLifecycle()
    var pickStart by remember { mutableStateOf(false) }
    var pickDeadline by remember { mutableStateOf(false) }

    LaunchedEffect(form.saved) {
        if (form.saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(if (courseId == null) "Add course" else "Edit course")
                        Text(
                            text = "Keep the details tidy and useful",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
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
        if (form.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                EmptyState(
                    icon = Icons.Rounded.HourglassEmpty,
                    title = "Loading course",
                    subtitle = "Preparing your notes, progress, and dates for editing.",
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FormSection(title = "Course details", icon = Icons.Outlined.School) {
                OutlinedTextField(
                    value = form.title,
                    onValueChange = { value -> viewModel.update { it.copy(title = value) } },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
                OutlinedTextField(
                    value = form.provider,
                    onValueChange = { value -> viewModel.update { it.copy(provider = value) } },
                    label = { Text("Provider (Udemy, Coursera…)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
                Text(
                    text = "Source type",
                    style = MaterialTheme.typography.labelLarge,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SourceType.entries.forEach { sourceType ->
                        FilterChip(
                            selected = form.sourceType == sourceType,
                            onClick = { viewModel.update { it.copy(sourceType = sourceType) } },
                            label = { Text(sourceType.label) },
                            colors = formChipColors(),
                        )
                    }
                }
            }

            FormSection(title = "Link & schedule", icon = Icons.Outlined.Link) {
                OutlinedTextField(
                    value = form.url,
                    onValueChange = { value -> viewModel.update { it.copy(url = value) } },
                    label = { Text("URL / deep-link") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
                DateButton(
                    label = "Start / purchase date",
                    value = DateUtils.formatDate(form.purchaseOrStartDate),
                    onClick = { pickStart = true },
                )
                DateButton(
                    label = "Deadline",
                    value = DateUtils.formatDate(form.deadline),
                    onClick = { pickDeadline = true },
                )
            }

            FormSection(title = "Momentum", icon = Icons.Rounded.Tune) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelLarge,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CourseStatus.entries.forEach { status ->
                        FilterChip(
                            selected = form.status == status,
                            onClick = { viewModel.update { it.copy(status = status) } },
                            label = { Text(status.label) },
                            colors = formChipColors(),
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = "${form.progressPercent.coerceIn(0, 100)}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Slider(
                    value = form.progressPercent.coerceIn(0, 100).toFloat(),
                    onValueChange = { value ->
                        viewModel.update { it.copy(progressPercent = value.toInt()) }
                    },
                    valueRange = 0f..100f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant,
                    ),
                )
            }

            FormSection(title = "Notes & tags", icon = Icons.AutoMirrored.Outlined.Notes) {
                OutlinedTextField(
                    value = form.tags,
                    onValueChange = { value -> viewModel.update { it.copy(tags = value) } },
                    label = { Text("Tags (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
                OutlinedTextField(
                    value = form.notes,
                    onValueChange = { value -> viewModel.update { it.copy(notes = value) } },
                    label = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp),
                    shape = MaterialTheme.shapes.medium,
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 14.dp),
                    ) {
                        Text("Reminder", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Keep this course visible in your due-soon dashboard",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = form.reminderEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.update { it.copy(reminderEnabled = enabled) }
                        },
                    )
                }
            }

            form.error?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 12.dp),
                        )
                    }
                }
            }

            Button(
                onClick = viewModel::save,
                enabled = !form.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                if (form.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                    Text("  Save course")
                }
            }
        }
    }

    if (pickStart) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateUtils.toPickerDateMillis(form.purchaseOrStartDate),
        )
        DatePickerDialog(
            onDismissRequest = { pickStart = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.update {
                            it.copy(
                                purchaseOrStartDate = DateUtils.fromPickerDateMillis(
                                    datePickerState.selectedDateMillis,
                                ),
                            )
                        }
                        pickStart = false
                    },
                ) {
                    Text("Set date")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.update { it.copy(purchaseOrStartDate = null) }
                        pickStart = false
                    },
                ) {
                    Text("Clear")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (pickDeadline) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateUtils.toPickerDateMillis(form.deadline),
        )
        DatePickerDialog(
            onDismissRequest = { pickDeadline = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.update {
                            it.copy(
                                deadline = DateUtils.fromPickerDateMillis(
                                    datePickerState.selectedDateMillis,
                                ),
                            )
                        }
                        pickDeadline = false
                    },
                ) {
                    Text("Set date")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.update { it.copy(deadline = null) }
                        pickDeadline = false
                    },
                ) {
                    Text("Clear")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 9.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun DateButton(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(19.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun formChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = MaterialTheme.colorScheme.surface,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
)
