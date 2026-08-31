package feelings.guide.ui.questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import feelings.guide.data.QuestionEntity
import feelings.guide.data.QuestionRepository
import feelings.guide.data.QuestionWithStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionListViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    val questions: StateFlow<List<QuestionWithStats>> = questionRepository.observeVisibleWithStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: StateFlow<Pair<Int, Int>> = questionRepository.observeSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0 to 0)

    init {
        viewModelScope.launch { questionRepository.relocalizeBuiltInQuestionsIfLocaleChanged() }
    }

    fun createQuestion(text: String) {
        viewModelScope.launch { questionRepository.createQuestion(text.trim()) }
    }

    fun updateQuestion(question: QuestionEntity, text: String) {
        viewModelScope.launch { questionRepository.updateQuestion(question.id, text.trim()) }
    }

    fun deleteQuestion(question: QuestionEntity, alsoDeleteAnswers: Boolean) {
        viewModelScope.launch { questionRepository.deleteQuestion(question.id, alsoDeleteAnswers) }
    }

    fun hideQuestion(question: QuestionEntity) {
        viewModelScope.launch { questionRepository.hideQuestion(question.id) }
    }
}
