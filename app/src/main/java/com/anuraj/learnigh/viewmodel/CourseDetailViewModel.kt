package com.anuraj.learnigh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.repository.CourseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val courseId: Long,
    private val repository: CourseRepository,
) : ViewModel() {

    val course: StateFlow<CourseEntity?> = repository.observeById(courseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun updateProgress(percent: Int) {
        viewModelScope.launch {
            val current = repository.getById(courseId) ?: return@launch
            val clamped = percent.coerceIn(0, 100)
            val status = when {
                clamped >= 100 -> CourseStatus.COMPLETED.name
                clamped > 0 && current.status in listOf(
                    CourseStatus.NOT_STARTED.name,
                    CourseStatus.WISHLIST.name,
                ) -> CourseStatus.IN_PROGRESS.name
                else -> current.status
            }
            repository.upsert(current.copy(progressPercent = clamped, status = status))
        }
    }

    fun updateStatus(status: CourseStatus) {
        viewModelScope.launch {
            val current = repository.getById(courseId) ?: return@launch
            val progress = if (status == CourseStatus.COMPLETED) 100 else current.progressPercent
            repository.upsert(current.copy(status = status.name, progressPercent = progress))
        }
    }

    fun delete() {
        viewModelScope.launch { repository.delete(courseId) }
    }

    companion object {
        fun factory(id: Long, repo: CourseRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CourseDetailViewModel(id, repo) as T
            }
    }
}
