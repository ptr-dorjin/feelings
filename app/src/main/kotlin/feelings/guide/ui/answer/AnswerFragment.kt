package feelings.guide.ui.answer

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import feelings.guide.R
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.databinding.AnswerBinding
import feelings.guide.question.Question
import feelings.guide.question.QuestionService
import java.time.LocalDateTime
import java.util.*

class AnswerFragment(
    private var questionId: Long = -1,
    internal var answerId: Long = -1
) : Fragment() {

    private var answer: Answer? = null

    private val isInvalid: Boolean
        get() = binding.answerText.text.trim().isEmpty()

    private var _binding: AnswerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.save_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
            when (menuItem.itemId) {
                android.R.id.home -> navigateBack(false, answer?.id)
                R.id.save -> save()
                else -> false
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AnswerBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(menuProvider)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val question = QuestionService.getById(requireContext(), questionId)
        answer = if (answerId == -1L) null else AnswerStore.getById(requireContext(), answerId)

        if (question == null) {
            Snackbar.make(binding.answerLayout, "Error: cannot find question", Snackbar.LENGTH_LONG)
                .show()
            return
        }

        setUpQuestionText(question)
        setUpQuestionDescription(question)
        setUpAnswerText()
        setUpFeelingsList()
    }

    private fun setUpQuestionText(question: Question) {
        binding.questionTextOnAnswer.text = question.text
        binding.questionTextOnAnswer.movementMethod = ScrollingMovementMethod()
    }

    private fun setUpQuestionDescription(question: Question) {
        val description = question.description
        if (!description.isNullOrEmpty()) {
            binding.questionDescriptionOnAnswer.text = description
            binding.questionDescriptionOnAnswer.visibility = View.VISIBLE
        }
    }

    private fun setUpAnswerText() {
        val isEdit = answer != null
        if (isEdit) {
            binding.answerText.setText(answer!!.answerText)
        }
        if (questionId != QuestionService.FEELINGS_ID) {
            binding.answerText.requestFocus()
        }
    }

    private fun setUpFeelingsList() {
        if (questionId != QuestionService.FEELINGS_ID) return

        val feelingsGroups = ArrayList<String>()
        val mapFeelingsByGroup = HashMap<String, List<String>>()

        setUpFeelingsGroup(R.string.anger, R.array.anger_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.fear, R.array.fear_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(
            R.string.sadness,
            R.array.sadness_array,
            feelingsGroups,
            mapFeelingsByGroup
        )
        setUpFeelingsGroup(R.string.joy, R.array.joy_array, feelingsGroups, mapFeelingsByGroup)
        setUpFeelingsGroup(R.string.love, R.array.love_array, feelingsGroups, mapFeelingsByGroup)

        val adapter =
            FeelingsExpandableListAdapter(requireContext(), feelingsGroups, mapFeelingsByGroup)
        binding.feelingsListView.setAdapter(adapter)
        binding.feelingsListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val selected = adapter.getChild(groupPosition, childPosition) as String?
            val answer = binding.answerText.text
            if (answer.trim().isNotEmpty()) {
                answer.append(", ")
            }
            answer.append(selected)
            true
        }
        binding.feelingsListView.visibility = View.VISIBLE
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

    private fun save(): Boolean {
        if (isInvalid) {
            Snackbar.make(
                binding.answerLayout,
                R.string.msg_answer_text_empty,
                Snackbar.LENGTH_LONG
            ).show()
            return false
        }
        val text = binding.answerText.text.toString().trim()
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
            val msg =
                if (isNew) R.string.msg_answer_added_error else R.string.msg_answer_updated_error
            Snackbar.make(binding.answerLayout, msg, Snackbar.LENGTH_LONG).show()
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