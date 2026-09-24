package com.anuraj.learnigh.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import com.anuraj.learnigh.ui.theme.DangerRose
import com.anuraj.learnigh.ui.theme.IndigoLight
import com.anuraj.learnigh.ui.theme.IndigoPrimary
import com.anuraj.learnigh.ui.theme.MagentaAccent
import com.anuraj.learnigh.ui.theme.SuccessGreen
import com.anuraj.learnigh.ui.theme.VioletSecondary
import com.anuraj.learnigh.ui.theme.WarningAmber
import com.anuraj.learnigh.util.DateUtils

@Composable
fun CourseCard(
    course: CourseEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 4.dp,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f),
        ),
    ) {
        Box(
            modifier = Modifier.background(
                Brush.horizontalGradient(
                    listOf(
                        IndigoPrimary.copy(alpha = 0.09f),
                        Color.Transparent,
                    ),
                ),
            ),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SourceIcon(sourceType = SourceType.fromName(course.sourceType))
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = course.provider,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (course.reminderEnabled) {
                        Icon(
                            imageVector = Icons.Outlined.Alarm,
                            contentDescription = "Reminder on",
                            tint = MagentaAccent,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatusChip(status = CourseStatus.fromName(course.status))
                    Text(
                        text = DateUtils.dueLabel(course.deadline),
                        style = MaterialTheme.typography.labelMedium,
                        color = dueColor(course.deadline),
                    )
                }
                if (course.progressPercent > 0 || course.status == CourseStatus.IN_PROGRESS.name) {
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { course.progressPercent.coerceIn(0, 100) / 100f },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outlineVariant,
                            strokeCap = StrokeCap.Round,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "${course.progressPercent.coerceIn(0, 100)}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SourceIcon(sourceType: SourceType) {
    val icon: ImageVector = when (sourceType) {
        SourceType.YOUTUBE -> Icons.Outlined.VideoLibrary
        SourceType.APP_SUBSCRIPTION -> Icons.Outlined.Subscriptions
        SourceType.WEBSITE -> Icons.Outlined.Language
        SourceType.PAID_COURSE -> Icons.Outlined.School
        SourceType.OTHER_FREE -> Icons.Outlined.PlayCircle
    }
    val accent = when (sourceType) {
        SourceType.YOUTUBE -> DangerRose
        SourceType.APP_SUBSCRIPTION -> VioletSecondary
        SourceType.WEBSITE -> SuccessGreen
        SourceType.PAID_COURSE -> IndigoLight
        SourceType.OTHER_FREE -> WarningAmber
    }
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.34f), IndigoPrimary.copy(alpha = 0.24f)),
                ),
            )
            .border(
                width = 1.dp,
                color = accent.copy(alpha = 0.24f),
                shape = RoundedCornerShape(16.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = sourceType.label,
            tint = accent,
            modifier = Modifier.size(25.dp),
        )
    }
}

@Composable
fun StatusChip(status: CourseStatus) {
    val (background, foreground) = when (status) {
        CourseStatus.IN_PROGRESS -> IndigoPrimary.copy(alpha = 0.22f) to IndigoLight
        CourseStatus.COMPLETED -> SuccessGreen.copy(alpha = 0.2f) to SuccessGreen
        CourseStatus.PAUSED -> WarningAmber.copy(alpha = 0.2f) to WarningAmber
        CourseStatus.EXPIRED -> DangerRose.copy(alpha = 0.2f) to DangerRose
        CourseStatus.WISHLIST -> VioletSecondary.copy(alpha = 0.22f) to MagentaAccent
        CourseStatus.NOT_STARTED -> {
            MaterialTheme.colorScheme.outlineVariant to MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    Surface(
        color = background,
        contentColor = foreground,
        shape = CircleShape,
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
        )
    }
}

@Composable
fun dueColor(deadline: Long?): Color {
    val days = DateUtils.daysUntil(deadline) ?: return MaterialTheme.colorScheme.onSurfaceVariant
    return when {
        days < 0 -> DangerRose
        days <= 2 -> WarningAmber
        days <= 7 -> IndigoLight
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        if (action != null && onAction != null) {
            TextButton(
                onClick = onAction,
                modifier = Modifier.heightIn(min = 40.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(text = action)
            }
        }
    }
}

@Composable
fun GradientHero(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2A1F5E),
                        Color(0xFF5B2AA7),
                        Color(0xFF1E3A5F),
                    ),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.13f), shape)
            .padding(22.dp),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        content()
    }
}

@Composable
fun StatPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.09f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 14.dp, vertical = 11.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = accent,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.74f),
        )
    }
}

@Composable
fun AddCourseFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp,
        ),
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add course",
        )
    }
}
