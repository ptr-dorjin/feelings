package feelings.guide.ui.answer

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import feelings.guide.R
import feelings.guide.data.answer.Answer
import feelings.guide.data.question.FEELINGS_ID
import feelings.guide.data.question.Question
import kotlinx.android.synthetic.main.answer.*
import org.threeten.bp.LocalDateTime
import java.util.*

class AnswerFragment(
    private var questionId: Long = -1,
    internal var answerId: Long = -1
) : Fragment() {

    private var answer: Answer? = null

    private val isInvalid: Boolean
        get() = answerText.text.trim().isEmpty()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.answer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        val question = QuestionService.getById(requireContext(), questionId)
        answer = if (answerId == -1L) null else AnswerStore.getById(requireContext(), answerId)

        if (question == null) {
            Snackbar.make(answerLayout, "Error: cannot find question", Snackbar.LENGTH_LONG).show()
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
        if (questionId != FEELINGS_ID) {
            answerText.requestFocus()
        }
    }

    private fun setUpFeelingsList() {
        if (questionId != FEELINGS_ID) return

        val feelingsGroups = ArrayList<String>()
        val mapFeelingsByGroup = HashMap<String, List<String>>()

        setUpFeelingsGroup(R.string.anger, R.array.anger_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.fear, R.array.fear_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.sadness, R.array.sadness_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.joy, R.array.joy_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.love, R.array.love_array, feelingsGroups, mapFeelingsByGroup)

        val adapter = FeelingsExpandableListAdapter(requireContext(), feelingsGroups, mapFeelingsByGroup)
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
        mapFeelingsByGroup[group] = listOf(*resources.getStringArray(feelingsArrayId))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> navigateBack(false, answer?.id)
        R.id.save -> save()
        else -> super.onOptionsItemSelected(item)
    }

    private fun save(): Boolean {
        if (isInvalid) {
            Snackbar.make(answerLayout, R.string.msg_answer_text_empty, Snackbar.LENGTH_LONG).show()
            return false
        }
        val text = answerText.text.toString().trim()
        val saved: Boolean
        val isNew = answer == null
        if (isNew) {
            answer = Answer(questionId, LocalDateTime.now(), text)
            saved = AnswerStore.saveAnswer(requireContext(), answer!!)
        } else {
            answer!!.answerText = text
            saved = AnswerStore.updateAnswer(requireContext(), answer!!)
        }

        if (saved) {
            navigateBack(true, answer!!.id)
        } else {
            val msg = if (isNew) R.string.msg_answer_added_error else R.string.msg_answer_updated_error
            Snackbar.make(answerLayout, msg, Snackbar.LENGTH_LONG).show()
        }
        return saved
    }

    private fun navigateBack(updated: Boolean, answerId: Long?): Boolean {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            (requireActivity() as AnswerActivity).navigateBack(updated, answerId)
        }
        return true
    }
}