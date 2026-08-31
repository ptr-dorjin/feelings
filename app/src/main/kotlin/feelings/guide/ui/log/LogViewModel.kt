package feelings.guide.ui.log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import feelings.guide.answer.AnswerForExport
import feelings.guide.data.AnswerEntity
import feelings.guide.data.AnswerRepository
import feelings.guide.data.QuestionRepository
import feelings.guide.settings.AppSettings
import feelings.guide.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LogRow(
    val answer: AnswerEntity,
    val questionText: String,
)

data class LogUiState(
    val rows: List<LogRow> = emptyList(),
    val questionText: String? = null,
    val settings: AppSettings = AppSettings(),
)

@HiltViewModel
class LogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val answerRepository: AnswerRepository,
    private val questionRepository: QuestionRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val questionId: Long? = (savedStateHandle.get<Long>("questionId"))?.takeIf { it > 0 }
    val isFullLog: Boolean get() = questionId == null

    val uiState: StateFlow<LogUiState> = combine(
        if (questionId != null) answerRepository.observeByQuestion(questionId) else answerRepository.observeAll(),
        questionRepository.observeAll(),
        settingsRepository.settings,
    ) { answers, questions, settings ->
        val textById = questions.associate { it.id to it.text }
        LogUiState(
            rows = answers.map { LogRow(it, textById[it.questionId] ?: "") },
            questionText = questionId?.let { id -> textById[id] },
            settings = settings,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LogUiState())

    fun deleteAnswer(answer: AnswerEntity) {
        viewModelScope.launch { answerRepository.deleteById(answer.id) }
    }

    fun undoDelete(answer: AnswerEntity) {
        viewModelScope.launch { answerRepository.restoreAnswer(answer) }
    }

    fun clearLog() {
        viewModelScope.launch {
            if (questionId != null) answerRepository.clearForQuestion(questionId) else answerRepository.clearAll()
        }
    }

    fun clearDeleted() {
        viewModelScope.launch { answerRepository.clearForDeletedOrHiddenQuestions() }
    }

    suspend fun getAnswersForExport(): List<AnswerForExport> = answerRepository.exportAnswers(questionId)
}
