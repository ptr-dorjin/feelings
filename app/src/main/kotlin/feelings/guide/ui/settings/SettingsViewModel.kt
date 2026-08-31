package feelings.guide.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import feelings.guide.data.QuestionRepository
import feelings.guide.settings.AppSettings
import feelings.guide.settings.SettingsRepository
import feelings.guide.settings.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val hiddenBuiltInCount: StateFlow<Int> = questionRepository.observeHiddenBuiltInCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setDateFormatPattern(pattern: String) {
        viewModelScope.launch { settingsRepository.setDateFormatPattern(pattern) }
    }

    fun setTimeFormatPattern(pattern: String) {
        viewModelScope.launch { settingsRepository.setTimeFormatPattern(pattern) }
    }

    fun restoreHiddenBuiltInQuestions() {
        viewModelScope.launch { questionRepository.restoreHidden() }
    }
}
