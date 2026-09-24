package com.anuraj.learnigh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import com.anuraj.learnigh.data.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditCourseForm(
    val id: Long = 0,
    val title: String = "",
    val provider: String = "",
    val sourceType: SourceType = SourceType.PAID_COURSE,
    val url: String = "",
    val purchaseOrStartDate: Long? = null,
    val deadline: Long? = null,
    val status: CourseStatus = CourseStatus.NOT_STARTED,
    val progressPercent: Int = 0,
    val notes: String = "",
    val tags: String = "",
    val reminderEnabled: Boolean = false,
    val reminderEdited: Boolean = false,
    val createdAt: Long? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

class EditCourseViewModel(
    private val courseId: Long?,
    private val repository: CourseRepository,
    private val defaultReminders: Flow<Boolean>? = null,
) : ViewModel() {

    private val _form = MutableStateFlow(EditCourseForm(isLoading = courseId != null))
    val form: StateFlow<EditCourseForm> = _form.asStateFlow()

    init {
        if (courseId == null && defaultReminders != null) {
            viewModelScope.launch {
                val enabled = defaultReminders.first()
                _form.update {
                    if (it.reminderEdited) it else it.copy(reminderEnabled = enabled)
                }
            }
        }
        if (courseId != null) {
            viewModelScope.launch {
                val course = repository.getById(courseId)
                if (course != null) {
                    _form.value = EditCourseForm(
                        id = course.id,
                        title = course.title,
                        provider = course.provider,
                        sourceType = SourceType.fromName(course.sourceType),
                        url = course.url,
                        purchaseOrStartDate = course.purchaseOrStartDate,
                        deadline = course.deadline,
                        status = CourseStatus.fromName(course.status),
                        progressPercent = course.progressPercent,
                        notes = course.notes,
                        tags = course.tags,
                        reminderEnabled = course.reminderEnabled,
                        createdAt = course.createdAt,
                    )
                } else {
                    _form.update {
                        it.copy(isLoading = false, error = "Course not found")
                    }
                }
            }
        }
    }

    fun update(transform: (EditCourseForm) -> EditCourseForm) {
        _form.update { current ->
            val updated = transform(current)
            updated.copy(
                error = null,
                saved = false,
                reminderEdited = current.reminderEdited ||
                    current.reminderEnabled != updated.reminderEnabled,
            )
        }
    }

    fun save() {
        val form = _form.value
        if (form.isSaving || form.isLoading) return
        if (form.title.isBlank() || form.provider.isBlank()) {
            _form.update { it.copy(error = "Title and provider are required") }
            return
        }

        viewModelScope.launch {
            _form.update { it.copy(isSaving = true, error = null) }
            runCatching {
                repository.upsert(
                    CourseEntity(
                        id = form.id,
                        title = form.title.trim(),
                        provider = form.provider.trim(),
                        sourceType = form.sourceType.name,
                        url = form.url.trim(),
                        purchaseOrStartDate = form.purchaseOrStartDate,
                        deadline = form.deadline,
                        status = form.status.name,
                        progressPercent = form.progressPercent.coerceIn(0, 100),
                        notes = form.notes.trim(),
                        tags = form.tags.trim(),
                        reminderEnabled = form.reminderEnabled,
                        createdAt = form.createdAt ?: System.currentTimeMillis(),
                    ),
                )
            }.onSuccess {
                _form.update { it.copy(isSaving = false, saved = true, error = null) }
            }.onFailure { error ->
                _form.update {
                    it.copy(
                        isSaving = false,
                        error = error.message ?: "Unable to save this course",
                    )
                }
            }
        }
    }

    companion object {
        fun factory(
            id: Long?,
            repo: CourseRepository,
            remindersDefault: Flow<Boolean>? = null,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                EditCourseViewModel(id, repo, remindersDefault) as T
        }
    }
}
