package feelings.guide.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private val LAST_LOCALE_KEY = stringPreferencesKey("last_locale")

@Singleton
class QuestionRepository @Inject constructor(
    private val database: FeelingsDatabase,
    private val questionDao: QuestionDao,
    private val answerDao: AnswerDao,
    private val settingsDataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context,
) {
    fun observeVisibleWithStats(): Flow<List<QuestionWithStats>> =
        combine(questionDao.observeVisible(), answerDao.observeStats()) { questions, stats ->
            val statsByQuestion = stats.associateBy { it.questionId }
            questions.map { question ->
                val stat = statsByQuestion[question.id]
                QuestionWithStats(question, stat?.count ?: 0, stat?.lastAnsweredAt)
            }
        }

    fun observeById(id: Long): Flow<QuestionEntity?> = questionDao.observeById(id)

    suspend fun getById(id: Long): QuestionEntity? = questionDao.getById(id)

    fun observeAll(): Flow<List<QuestionEntity>> = questionDao.observeAll()

    suspend fun getAllAsList(): List<QuestionEntity> = questionDao.getAllAsList()

    fun observeHiddenBuiltInCount(): Flow<Int> = questionDao.observeHiddenBuiltInCount()

    fun observeSummary(): Flow<Pair<Int, Int>> =
        combine(questionDao.observeVisible(), answerDao.observeTotalCount()) { questions, answerCount ->
            answerCount to questions.size
        }

    suspend fun createQuestion(text: String): Long =
        questionDao.insert(QuestionEntity(text = text, isUser = true))

    suspend fun updateQuestion(id: Long, text: String) {
        val existing = questionDao.getById(id) ?: return
        if (!existing.isUser) return
        questionDao.update(existing.copy(text = text))
    }

    /** Atomically soft-deletes a user question and, optionally, all of its answers. */
    suspend fun deleteQuestion(id: Long, alsoDeleteAnswers: Boolean) {
        database.withTransaction {
            questionDao.softDeleteUserQuestion(id)
            if (alsoDeleteAnswers) {
                answerDao.deleteByQuestionId(id)
            }
        }
    }

    suspend fun hideQuestion(id: Long) = questionDao.hideBuiltIn(id)

    suspend fun restoreHidden() = questionDao.restoreHidden()

    /**
     * Built-in question text was localized when it was inserted. Since the app no longer lets
     * the user pick a language in-app, this re-localizes it whenever the system locale has
     * changed since the last time the app ran.
     */
    suspend fun relocalizeBuiltInQuestionsIfLocaleChanged() {
        val currentLocale = Locale.getDefault().toLanguageTag()
        val lastLocale = settingsDataStore.data.first()[LAST_LOCALE_KEY]
        if (lastLocale == currentLocale) return

        for (q in BUILT_IN_QUESTIONS) {
            questionDao.relocalize(q.code, context.getString(q.textResId))
        }
        settingsDataStore.edit { it[LAST_LOCALE_KEY] = currentLocale }
    }
}
