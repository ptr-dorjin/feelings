package feelings.guide.ui.answer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import feelings.guide.R
import feelings.guide.data.AnswerEntity
import feelings.guide.data.AnswerRepository
import feelings.guide.data.QuestionEntity
import feelings.guide.data.QuestionRepository
import feelings.guide.util.SnackbarEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnswerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val snackbarEventBus: SnackbarEventBus,
) : ViewModel() {

    private val questionId: Long = checkNotNull(savedStateHandle["questionId"])
    private val answerId: Long? = (savedStateHandle.get<Long>("answerId"))?.takeIf { it > 0 }

    val question: StateFlow<QuestionEntity?> = questionRepository.observeById(questionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _answerText = MutableStateFlow("")
    val answerText: StateFlow<String> = _answerText.asStateFlow()

    val isEditing: Boolean get() = answerId != null

    init {
        answerId?.let { id ->
            viewModelScope.launch {
                answerRepository.getById(id)?.let { existing: AnswerEntity ->
                    _answerText.value = existing.answerText ?: ""
                }
            }
        }
    }

    fun onAnswerTextChanged(text: String) {
        _answerText.value = text
    }

    fun appendFeelingWord(word: String) {
        val current = _answerText.value
        _answerText.value = if (current.isBlank()) word else "$current, $word"
    }

    fun save(onSaved: () -> Unit) {
        val text = _answerText.value.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            val id = answerId
            if (id != null) {
                answerRepository.updateAnswer(id, text)
                snackbarEventBus.emit(R.string.msg_answer_updated_success)
            } else {
                answerRepository.saveNewAnswer(questionId, text)
                snackbarEventBus.emit(R.string.msg_answer_added_success)
            }
            onSaved()
        }
    }
}
