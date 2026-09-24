package com.anuraj.learnigh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import com.anuraj.learnigh.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val isLoading: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

class EditCourseViewModel(
    private val courseId: Long?,
    private val repository: CourseRepository,
) : ViewModel() {

    private val _form = MutableStateFlow(EditCourseForm(isLoading = courseId != null))
    val form: StateFlow<EditCourseForm> = _form.asStateFlow()

    init {
        if (courseId != null) {
            viewModelScope.launch {
                val c = repository.getById(courseId)
                if (c != null) {
                    _form.value = EditCourseForm(
                        id = c.id,
                        title = c.title,
                        provider = c.provider,
                        sourceType = SourceType.fromName(c.sourceType),
                        url = c.url,
                        purchaseOrStartDate = c.purchaseOrStartDate,
                        deadline = c.deadline,
                        status = CourseStatus.fromName(c.status),
                        progressPercent = c.progressPercent,
                        notes = c.notes,
                        tags = c.tags,
                        reminderEnabled = c.reminderEnabled,
                        isLoading = false,
                    )
                } else {
                    _form.update { it.copy(isLoading = false, error = "Course not found") }
                }
            }
        }
    }

    fun update(transform: (EditCourseForm) -> EditCourseForm) {
        _form.update(transform)
    }

    fun save() {
        val f = _form.value
        if (f.title.isBlank() || f.provider.isBlank()) {
            _form.update { it.copy(error = "Title and provider are required") }
            return
        }
        viewModelScope.launch {
            repository.upsert(
                CourseEntity(
                    id = f.id,
                    title = f.title.trim(),
                    provider = f.provider.trim(),
                    sourceType = f.sourceType.name,
                    url = f.url.trim(),
                    purchaseOrStartDate = f.purchaseOrStartDate,
                    deadline = f.deadline,
                    status = f.status.name,
                    progressPercent = f.progressPercent.coerceIn(0, 100),
                    notes = f.notes.trim(),
                    tags = f.tags.trim(),
                    reminderEnabled = f.reminderEnabled,
                ),
            )
            _form.update { it.copy(saved = true, error = null) }
        }
    }

    companion object {
        fun factory(id: Long?, repo: CourseRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    EditCourseViewModel(id, repo) as T
            }
    }
}
