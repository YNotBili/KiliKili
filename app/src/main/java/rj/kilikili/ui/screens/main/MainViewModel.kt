package rj.kilikili.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.account.AccountManager
import rj.kilikili.data.setting.LocalData
import rj.kilikili.data.setting.edit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MainUiState {
    object Loading : MainUiState
    object NeedsSetup : MainUiState
    object NeedsLogin : MainUiState
    object Ready : MainUiState
}

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val uiState: StateFlow<MainUiState> = combine(
        LocalData.settingsStateFlow,
        AccountManager.repository.activeAccount
    ) { settings, account ->
        when {
            settings == null -> MainUiState.Loading

            settings.firstRun -> MainUiState.NeedsSetup

            account == null || account.accountId == 0L -> MainUiState.NeedsLogin

            else -> MainUiState.Ready
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState.Loading
        )

    fun onSetupComplete() {
        viewModelScope.launch {
            LocalData.edit {
                firstRun = false
            }
        }
    }
}