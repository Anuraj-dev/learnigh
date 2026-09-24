package com.anuraj.learnigh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.repository.CourseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class CalendarViewModel(
    repository: CourseRepository,
) : ViewModel() {

    val deadlines: StateFlow<List<CourseEntity>> = repository.observeWithDeadlines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val byMonth: StateFlow<Map<String, List<CourseEntity>>> = repository.observeWithDeadlines()
        .map { list ->
            list.groupBy { course ->
                val cal = Calendar.getInstance()
                cal.timeInMillis = course.deadline!!
                "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}"
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    companion object {
        fun factory(repo: CourseRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CalendarViewModel(repo) as T
            }
    }
}
