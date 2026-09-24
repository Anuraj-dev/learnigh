package com.anuraj.learnigh.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.anuraj.learnigh.ui.theme.IndigoPrimary
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
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SourceIcon(sourceType = SourceType.fromName(course.sourceType))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = course.provider,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (course.reminderEnabled) {
                    Icon(
                        Icons.Outlined.Alarm,
                        contentDescription = "Reminder on",
                        tint = VioletSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
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
            if (course.progressPercent > 0 ||
                course.status == CourseStatus.IN_PROGRESS.name
            ) {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { course.progressPercent / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = IndigoPrimary,
                        trackColor = MaterialTheme.colorScheme.outline,
                        strokeCap = StrokeCap.Round,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "${course.progressPercent}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(IndigoPrimary.copy(alpha = 0.35f), VioletSecondary.copy(alpha = 0.35f)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = sourceType.label, tint = IndigoPrimary)
    }
}

@Composable
fun StatusChip(status: CourseStatus) {
    val (bg, fg) = when (status) {
        CourseStatus.IN_PROGRESS -> IndigoPrimary.copy(0.2f) to IndigoPrimary
        CourseStatus.COMPLETED -> SuccessGreen.copy(0.2f) to SuccessGreen
        CourseStatus.PAUSED -> WarningAmber.copy(0.2f) to WarningAmber
        CourseStatus.EXPIRED -> DangerRose.copy(0.2f) to DangerRose
        CourseStatus.WISHLIST -> VioletSecondary.copy(0.2f) to VioletSecondary
        CourseStatus.NOT_STARTED -> MaterialTheme.colorScheme.outline to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = status.label,
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
fun dueColor(deadline: Long?): Color {
    val days = DateUtils.daysUntil(deadline) ?: return MaterialTheme.colorScheme.onSurfaceVariant
    return when {
        days < 0 -> DangerRose
        days <= 2 -> WarningAmber
        days <= 7 -> IndigoPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (action != null && onAction != null) {
            Text(
                text = action,
                style = MaterialTheme.typography.labelLarge,
                color = IndigoPrimary,
                modifier = Modifier.clickable(onClick = onAction),
            )
        }
    }
}

@Composable
fun GradientHero(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2A1F5E),
                        Color(0xFF4C1D95),
                        Color(0xFF1E3A5F),
                    ),
                ),
            )
            .padding(20.dp),
    ) {
        content()
    }
}

@Composable
fun StatPill(label: String, value: String, accent: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = accent)
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(0.7f))
    }
}
