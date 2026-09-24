package com.anuraj.learnigh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.local.SettingsDataStore
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.repository.CourseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val displayName: String = "Raja",
    val streakDays: Int = 0,
    val inProgress: List<CourseEntity> = emptyList(),
    val dueSoon: List<CourseEntity> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
)

class HomeViewModel(
    repository: CourseRepository,
    settings: SettingsDataStore,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeAll(),
        settings.displayName,
        settings.streakDays,
    ) { courses, name, streak ->
        val now = System.currentTimeMillis()
        val weekAhead = now + 7L * 24 * 60 * 60 * 1000
        HomeUiState(
            displayName = name,
            streakDays = streak,
            inProgress = courses.filter { it.status == CourseStatus.IN_PROGRESS.name },
            dueSoon = courses
                .filter {
                    it.deadline != null &&
                        it.status !in listOf(
                            CourseStatus.COMPLETED.name,
                            CourseStatus.EXPIRED.name,
                        ) &&
                        it.deadline!! <= weekAhead
                }
                .sortedBy { it.deadline },
            completedCount = courses.count { it.status == CourseStatus.COMPLETED.name },
            totalCount = courses.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    companion object {
        fun factory(repo: CourseRepository, settings: SettingsDataStore) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    HomeViewModel(repo, settings) as T
            }
    }
}
