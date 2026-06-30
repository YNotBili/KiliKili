package rj.kilikili.ui.screens.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.proto.AppSettings
import rj.kilikili.data.proto.NightMode
import rj.kilikili.data.proto.PlayerSettings
import rj.kilikili.data.setting.LocalData
import rj.kilikili.data.setting.edit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    val settingsState: StateFlow<AppSettings?> = LocalData.settingsStateFlow

    fun updateUiScale(value: Float) {
        viewModelScope.launch {
            LocalData.edit {
                uiSettings = uiSettings.edit { uiScale = value.coerceIn(0.25f, 5.0f) }
            }
        }
    }

    fun updateDensity(value: Int) {
        viewModelScope.launch {
            LocalData.edit {
                uiSettings = uiSettings.edit { density = if (value <= 72) 0 else value }
            }
        }
    }

    fun updateNightMode(value: NightMode) {
        viewModelScope.launch {
            LocalData.edit {
                theme = theme.edit { nightMode = value }
            }
        }
    }

    /**
     * 切换界面模式 (WEAR / PHONE)。设置后 [rj.kilikili.uiType] 通过 settingsStateFlow 同步生效。
     */
    fun updateUiType(value: rj.kilikili.UiType) {
        viewModelScope.launch {
            LocalData.edit {
                uiType = value.ordinal
            }
        }
    }

    fun updateFollowSystemAccent(enabled: Boolean) {
        viewModelScope.launch {
            LocalData.edit {
                theme = theme.edit { followSystemAccent = enabled }
            }
        }
    }

    fun updateRoundMode(enabled: Boolean) {
        viewModelScope.launch {
            LocalData.edit {
                uiSettings = uiSettings.edit { roundMode = enabled }
            }
        }
    }

    fun updateAnimations(enabled: Boolean) {
        viewModelScope.launch {
            LocalData.edit {
                theme = theme.edit { animationsEnabled = enabled }
            }
        }
    }

    fun updateDisableFullscreenDialog(enabled: Boolean) {
        viewModelScope.launch {
            LocalData.edit {
                theme = theme.edit { fullScreenDialogDisabled = enabled }
            }
        }
    }

    fun updateNewLoadingWidget(enabled: Boolean) {
        viewModelScope.launch {
            LocalData.edit {
                theme = theme.edit { newLoadingWidgetEnabled = enabled }
            }
        }
    }

    fun updateColorTheme(color: String) {
        viewModelScope.launch {
            LocalData.edit {
                theme = theme.edit { colorTheme = color }
            }
        }
    }

    fun updatePlayerSettings(settings: PlayerSettings) {
        viewModelScope.launch {
            LocalData.edit {
                playerSettings = settings
            }
        }
    }

    fun updateLanguage(languageTag: String) {
        viewModelScope.launch {
            LocalData.edit {
                language = languageTag
            }
        }
    }

    fun updateVideoCardBackgroundStyle(enabled: Boolean) {
        viewModelScope.launch {
            LocalData.edit {
                uiSettings = uiSettings.edit { videoCardBackgroundStyle = enabled }
            }
        }
    }

    fun updateUserProfileBackgroundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            LocalData.edit {
                uiSettings = uiSettings.edit { userProfileBackgroundEnabled = enabled }
            }
        }
    }

    fun updateCollectionCardBackgroundStyle(enabled: Boolean) {
        viewModelScope.launch {
            LocalData.edit {
                uiSettings = uiSettings.edit { collectionCardBackgroundStyle = enabled }
            }
        }
    }

    fun updateFavoriteFolderCardBackgroundStyle(enabled: Boolean) {
        viewModelScope.launch {
            LocalData.edit {
                uiSettings = uiSettings.edit { favoriteFolderCardBackgroundStyle = enabled }
            }
        }
    }

    fun clearAllSettings() {
        viewModelScope.launch {
            LocalData.clearAllSettings()
        }
    }
}