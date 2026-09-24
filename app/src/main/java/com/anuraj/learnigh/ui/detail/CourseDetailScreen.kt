package com.anuraj.learnigh.ui.detail

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.ui.components.EmptyState
import com.anuraj.learnigh.ui.components.GradientHero
import com.anuraj.learnigh.ui.components.SourceIcon
import com.anuraj.learnigh.ui.components.StatusChip
import com.anuraj.learnigh.ui.theme.DangerRose
import com.anuraj.learnigh.util.DateUtils
import com.anuraj.learnigh.viewmodel.CourseDetailViewModel
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CourseDetailScreen(
    courseId: Long,
    repository: CourseRepository,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
) {
    val viewModel: CourseDetailViewModel = viewModel(
        factory = CourseDetailViewModel.factory(courseId, repository),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val course = uiState.course
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDelete by remember { mutableStateOf(false) }
    var sliderActive by remember { mutableStateOf(false) }
    var progressDraft by remember(course?.id) {
        mutableStateOf(course?.progressPercent?.toFloat() ?: 0f)
    }

    LaunchedEffect(course?.progressPercent) {
        if (!sliderActive) progressDraft = course?.progressPercent?.toFloat() ?: 0f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(courseId) }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit course")
                    }
                    IconButton(onClick = { showDelete = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete course",
                            tint = DangerRose,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { contentPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyState(
                        icon = Icons.Rounded.HourglassEmpty,
                        title = "Loading course",
                        subtitle = "Gathering the details from your offline library.",
                    )
                }
            }

            course == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyState(
                        icon = Icons.Rounded.SearchOff,
                        title = "Course not found",
                        subtitle = "It may have been removed from your offline library.",
                        actionLabel = "Go back",
                        onAction = onBack,
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 36.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    GradientHero {
                        Column {
                            Text(
                                text = "COURSE SNAPSHOT",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.68f),
                            )
                            Spacer(Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SourceIcon(sourceType = SourceType.fromName(course.sourceType))
                                Column(modifier = Modifier.padding(start = 14.dp)) {
                                    Text(
                                        text = course.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = course.provider,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.72f),
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            StatusChip(status = CourseStatus.fromName(course.status))
                        }
                    }

                    if (course.url.isNotBlank()) {
                        Button(
                            onClick = {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(course.url)))
                                }.onFailure {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("No app can open this link")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null)
                            Text("  Open course link")
                        }
                    }

                    PremiumSection(title = "Progress", icon = Icons.Outlined.CalendarToday) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Learning progress",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "${progressDraft.roundToInt().coerceIn(0, 100)}%",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Slider(
                            value = progressDraft.coerceIn(0f, 100f),
                            onValueChange = {
                                sliderActive = true
                                progressDraft = it
                            },
                            onValueChangeFinished = {
                                val percent = progressDraft.roundToInt().coerceIn(0, 100)
                                if (percent != course.progressPercent) viewModel.updateProgress(percent)
                                sliderActive = false
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

                    PremiumSection(title = "Status", icon = Icons.Outlined.Tag) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CourseStatus.entries.forEach { status ->
                                FilterChip(
                                    selected = course.status == status.name,
                                    onClick = { viewModel.updateStatus(status) },
                                    label = { Text(status.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                )
                            }
                        }
                    }

                    PremiumSection(title = "Timeline", icon = Icons.Outlined.CalendarToday) {
                        DetailRow(
                            label = "Started / bought",
                            value = DateUtils.formatDate(course.purchaseOrStartDate),
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        DetailRow(
                            label = "Deadline",
                            value = DateUtils.formatDate(course.deadline),
                            supporting = DateUtils.dueLabel(course.deadline),
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        DetailRow(
                            label = "Reminder",
                            value = if (course.reminderEnabled) "On" else "Off",
                            icon = if (course.reminderEnabled) Icons.Outlined.Alarm else null,
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        DetailRow(label = "Tags", value = course.tags.ifBlank { "—" })
                    }

                    if (course.notes.isNotBlank()) {
                        PremiumSection(title = "Notes", icon = Icons.Outlined.Tag) {
                            Text(
                                text = course.notes,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Delete course?") },
            text = { Text("This removes the course and its progress from this device.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDelete = false
                        viewModel.delete(onDeleted)
                    },
                ) {
                    Text("Delete", color = DangerRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun PremiumSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
private fun DetailRow(
    label: String,
    value: String,
    supporting: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(17.dp),
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
            if (supporting != null) {
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
