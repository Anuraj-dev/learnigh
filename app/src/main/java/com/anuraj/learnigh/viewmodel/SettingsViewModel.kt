package com.anuraj.learnigh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anuraj.learnigh.data.local.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val displayName: String = "Raja",
    val streakDays: Int = 0,
    val remindersDefault: Boolean = true,
)

class SettingsViewModel(
    private val settings: SettingsDataStore,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settings.displayName,
        settings.streakDays,
        settings.remindersDefault,
    ) { name, streak, reminders ->
        SettingsUiState(name, streak, reminders)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setDisplayName(name: String) {
        viewModelScope.launch { settings.setDisplayName(name) }
    }

    fun setRemindersDefault(enabled: Boolean) {
        viewModelScope.launch { settings.setRemindersDefault(enabled) }
    }

    companion object {
        fun factory(settings: SettingsDataStore) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SettingsViewModel(settings) as T
            }
    }
}
