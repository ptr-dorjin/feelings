package feelings.guide.data.question

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import feelings.guide.data.answer.AnswerRepository
import feelings.guide.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuestionViewModel(application: Application) : AndroidViewModel(application) {

    private val questionRepository: QuestionRepository
    private val answerRepository: AnswerRepository

    val allQuestions: LiveData<List<Question>>

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)

        val questionDao = database.questionDao()
        val answerDao = database.answerDao()

        questionRepository = QuestionRepository(questionDao, answerDao)
        answerRepository = AnswerRepository(answerDao)

        allQuestions = questionRepository.getAllQuestions()
    }

    fun getById(questionId: Long) = questionRepository.getById(questionId)

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun createQuestion(question: Question) = viewModelScope.launch(Dispatchers.IO) {
        questionRepository.createQuestion(question)
    }

    fun updateQuestion(questionUpdate: QuestionUpdate) = viewModelScope.launch(Dispatchers.IO) {
        questionRepository.updateQuestion(questionUpdate)
    }

    fun deleteQuestion(questionId: Long) = viewModelScope.launch(Dispatchers.IO) {
        questionRepository.deleteQuestion(questionId)
    }

    fun hideQuestion(questionId: Long) = viewModelScope.launch(Dispatchers.IO) {
        questionRepository.hideQuestion(questionId)
    }

    fun clearLogByQuestion(questionId: Long) = viewModelScope.launch(Dispatchers.IO) {
        answerRepository.deleteForQuestion(questionId)
    }

    fun hasAnswers(questionId: Long) = questionRepository.hasAnswers(questionId)
}