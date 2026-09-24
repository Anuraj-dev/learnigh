package com.anuraj.learnigh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import com.anuraj.learnigh.data.repository.CourseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class CoursesFilterState(
    val query: String = "",
    val status: CourseStatus? = null,
    val sourceType: SourceType? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class CoursesViewModel(
    private val repository: CourseRepository,
) : ViewModel() {

    private val filters = MutableStateFlow(CoursesFilterState())

    val filterState: StateFlow<CoursesFilterState> = filters

    val courses: StateFlow<List<CourseEntity>> = filters
        .flatMapLatest { f ->
            repository.observeFiltered(f.query, f.status, f.sourceType)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(q: String) {
        filters.value = filters.value.copy(query = q)
    }

    fun setStatus(status: CourseStatus?) {
        filters.value = filters.value.copy(status = status)
    }

    fun setSourceType(type: SourceType?) {
        filters.value = filters.value.copy(sourceType = type)
    }

    companion object {
        fun factory(repo: CourseRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CoursesViewModel(repo) as T
            }
    }
}
