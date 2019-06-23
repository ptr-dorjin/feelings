package feelings.guide.ui.answer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import feelings.guide.*
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.question.Question
import feelings.guide.question.QuestionService
import feelings.guide.ui.BaseActivity
import kotlinx.android.synthetic.main.answer_activity.*
import org.threeten.bp.LocalDateTime
import java.util.*
import java.util.Arrays.asList

class AnswerActivity : BaseActivity() {

    private var questionId: Long = 0
    private var answer: Answer? = null

    private val isInvalid: Boolean
        get() = answerText.text.trim().isEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_activity)

        val intent = intent
        questionId = intent?.getLongExtra(QUESTION_ID_PARAM, -1) ?: -1
        val question = QuestionService.getById(this, questionId)

        val answerId = intent?.getLongExtra(ANSWER_ID_PARAM, -1) ?: -1
        answer = if (answerId == -1L) null else AnswerStore.getById(this, answerId)

        if (question == null) {
            Snackbar.make(answerActivityLayout, "Error: cannot find question", Snackbar.LENGTH_LONG).show()
            return
        }

        setUpQuestionText(question)
        setUpQuestionDescription(question)
        setUpAnswerText()
        setUpFeelingsList()
    }

    private fun setUpQuestionText(question: Question) {
        questionTextOnAnswer.text = question.text
        questionTextOnAnswer.movementMethod = ScrollingMovementMethod()
    }

    private fun setUpQuestionDescription(question: Question) {
        val description = question.description
        if (!description.isNullOrEmpty()) {
            questionDescriptionOnAnswer.text = description
            questionDescriptionOnAnswer.visibility = View.VISIBLE
        }
    }

    private fun setUpAnswerText() {
        val isEdit = answer != null
        if (isEdit) {
            answerText.setText(answer!!.answerText)
        }
        if (questionId != QuestionService.FEELINGS_ID) {
            answerText.requestFocus()
        }
    }

    private fun setUpFeelingsList() {
        if (questionId != QuestionService.FEELINGS_ID) return

        val feelingsGroups = ArrayList<String>()
        val mapFeelingsByGroup = HashMap<String, List<String>>()

        setUpFeelingsGroup(R.string.anger, R.array.anger_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.fear, R.array.fear_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.sadness, R.array.sadness_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.joy, R.array.joy_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.love, R.array.love_array, feelingsGroups, mapFeelingsByGroup)

        val adapter = FeelingsExpandableListAdapter(this, feelingsGroups, mapFeelingsByGroup)
        feelingsListView.setAdapter(adapter)
        feelingsListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val selected = adapter.getChild(groupPosition, childPosition) as String?
            val answer = answerText.text
            if (answer.trim().isNotEmpty()) {
                answer.append(", ")
            }
            answer.append(selected)
            true
        }
        feelingsListView.visibility = View.VISIBLE
    }

    private fun setUpFeelingsGroup(
        feelingsGroupId: Int,
        feelingsArrayId: Int,
        feelingsGroups: MutableList<String>,
        mapFeelingsByGroup: MutableMap<String, List<String>>
    ) {
        val resources = resources
        val group = resources.getString(feelingsGroupId)
        feelingsGroups.add(group)
        mapFeelingsByGroup[group] = asList(*resources.getStringArray(feelingsArrayId))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.save -> save()
        else -> super.onOptionsItemSelected(item)
    }

    private fun save(): Boolean {
        if (isInvalid) {
            Snackbar.make(answerActivityLayout, R.string.msg_answer_text_empty, Snackbar.LENGTH_LONG).show()
            return false
        }
        val text = answerText.text.toString().trim()
        val saved: Boolean
        val isNew = answer == null
        if (isNew) {
            answer = Answer(questionId, LocalDateTime.now(), text)
            saved = AnswerStore.saveAnswer(this, answer!!)
        } else {
            answer!!.answerText = text
            saved = AnswerStore.updateAnswer(this, answer!!)
        }

        if (saved) {
            val data = Intent().apply {
                putExtra(REFRESH_ANSWER_LOG_RESULT_KEY, true)
                putExtra(UPDATED_ANSWER_ID_RESULT_KEY, answer!!.id)
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        } else {
            val msg = if (isNew) R.string.msg_answer_added_error else R.string.msg_answer_updated_error
            Snackbar.make(answerActivityLayout, msg, Snackbar.LENGTH_LONG).show()
        }
        return saved
    }

}
