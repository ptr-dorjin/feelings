package feelings.guide.data.answer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import feelings.guide.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnswerLogByQuestionViewModel(
        application: Application,
        val questionId: Long
) : AndroidViewModel(application) {

    private val answerRepository: AnswerRepository

    val answersByQuestion: LiveData<List<Answer>>

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        val answerDao = database.answerDao()
        answerRepository = AnswerRepository(answerDao)
        answersByQuestion = answerRepository.getAnswersByQuestionId(questionId)
    }

    fun getById(id: Long) = answerRepository.getById(id)

    fun updateAnswer(id: Long, answerText: String) = viewModelScope.launch(Dispatchers.IO) {
        answerRepository.updateAnswer(id, answerText)
    }

    fun deleteAnswer(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        answerRepository.deleteAnswer(id)
    }

    fun deleteForQuestion() = viewModelScope.launch(Dispatchers.IO) {
        answerRepository.deleteForQuestion(questionId)
    }
}