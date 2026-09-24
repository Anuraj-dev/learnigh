package com.anuraj.learnigh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.repository.CourseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val course: CourseEntity? = null,
)

class CourseDetailViewModel(
    private val courseId: Long,
    private val repository: CourseRepository,
) : ViewModel() {

    val uiState: StateFlow<CourseDetailUiState> = repository.observeById(courseId)
        .map { course -> CourseDetailUiState(isLoading = false, course = course) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            CourseDetailUiState(),
        )

    fun updateProgress(percent: Int) {
        viewModelScope.launch {
            repository.updateProgress(courseId, percent)
        }
    }

    fun updateStatus(status: CourseStatus) {
        viewModelScope.launch {
            repository.updateStatus(courseId, status)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            if (repository.delete(courseId)) onDeleted()
        }
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
