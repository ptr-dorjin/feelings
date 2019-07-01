package feelings.guide.ui.question

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import feelings.guide.*
import feelings.guide.answer.AnswerStore
import feelings.guide.question.Question
import feelings.guide.question.QuestionService
import feelings.guide.ui.BaseActivity
import feelings.guide.ui.log.AnswerLogActivity
import feelings.guide.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.question_edit_dialog.*
import kotlinx.android.synthetic.main.questions_activity.*

class QuestionsActivity : BaseActivity(),
    QuestionEditDialogListener,
    QuestionDeleteDialogListener,
    QuestionHideDialogListener,
    QuestionClearLogDialogListener {

    private lateinit var adapter: QuestionsAdapter
    private var changeQuestionId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.questions_activity)

        setUpFab()
        setUpRv()
    }

    private fun setUpFab() {
        questionFab.setOnClickListener { showEditQuestionDialog(null) }
    }

    private fun setUpRv() {
        questionRV.layoutManager = LinearLayoutManager(this)
        adapter = QuestionsAdapter(this)
        adapter.setHasStableIds(true)
        questionRV.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.questions_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_log -> {
                showAnswerLog(null)
                true
            }
            R.id.show_settings -> {
                showSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showPopupMenu(v: View) {
        val popup = PopupMenu(this, v)
        val questionId = v.getTag(R.id.tag_question_id) as Long
        val isUser = v.getTag(R.id.tag_is_user) as Boolean
        popup.inflate(
            when {
                isUser -> R.menu.questions_popup_menu_user
                questionId == QuestionService.FEELINGS_ID -> R.menu.questions_popup_menu_feelings
                else -> R.menu.questions_popup_menu_built_in
            }
        )
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.show_log_by_question -> {
                    popup.dismiss()
                    showAnswerLog(questionId)
                    true
                }
                R.id.edit -> {
                    popup.dismiss()
                    showEditQuestionDialog(questionId)
                    true
                }
                R.id.delete -> {
                    popup.dismiss()
                    showDeleteConfirmation(questionId)
                    true
                }
                R.id.hide -> {
                    popup.dismiss()
                    showHideConfirmation(questionId)
                    true
                }
                R.id.clear_log -> {
                    popup.dismiss()
                    showClearLogConfirmation(questionId)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showEditQuestionDialog(questionId: Long?) {
        changeQuestionId = questionId
        val fragmentManager = supportFragmentManager
        val dialogFragment = QuestionEditDialogFragment()
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, QuestionEditDialogFragment::class.java.simpleName)
        fragmentManager.executePendingTransactions() // to fetch inflated dialog
        setUpEditDialog(questionId, dialogFragment)
    }

    private fun setUpEditDialog(questionId: Long?, dialogFragment: DialogFragment) {
        val dialog: Dialog = dialogFragment.dialog!!
        val questionEditText = dialog.questionTextEdit
        val saveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

        if (questionId != null)
            questionEditText.setText(QuestionService.getQuestionText(this, questionId))
        else saveButton.isEnabled = false

        questionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                saveButton.isEnabled = !TextUtils.isEmpty(s)
            }
        })
    }

    private fun showDeleteConfirmation(questionId: Long) {
        changeQuestionId = questionId
        QuestionDeleteDialogFragment().show(supportFragmentManager, QuestionDeleteDialogFragment::class.java.simpleName)
    }

    private fun showHideConfirmation(questionId: Long) {
        changeQuestionId = questionId
        QuestionHideDialogFragment().show(supportFragmentManager, QuestionHideDialogFragment::class.java.simpleName)
    }

    private fun showClearLogConfirmation(questionId: Long) {
        changeQuestionId = questionId
        QuestionClearLogDialogFragment().show(supportFragmentManager, QuestionClearLogDialogFragment::class.java.simpleName)
    }

    private fun showAnswerLog(questionId: Long?) {
        startActivity(Intent(this, AnswerLogActivity::class.java).apply {
            questionId?.let {
                putExtra(QUESTION_ID_PARAM, questionId)
            }
        })
    }

    private fun showSettings() {
        startActivityForResult(Intent(this, SettingsActivity::class.java), SETTINGS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val needToRefresh = data!!.getBooleanExtra(REFRESH_QUESTIONS_RESULT_KEY, false)
            if (needToRefresh) {
                adapter.refreshAll()
            }
        } else if (requestCode == ADD_ANSWER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Snackbar.make(questionsActivityLayout, R.string.msg_answer_added_success, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onSaveClick(dialogFragment: DialogFragment) {
        val text = dialogFragment.dialog!!.questionTextEdit.text.toString().trim()
        if (text.isEmpty()) {
            Snackbar.make(questionsActivityLayout, R.string.msg_question_text_empty, Snackbar.LENGTH_LONG).show()
            return
        }

        if (changeQuestionId == null) performServiceAction(
            QuestionService.createQuestion(this, Question(text)) != -1L,
            R.string.msg_question_create_success,
            R.string.msg_question_create_error
        ) else performServiceAction(
            QuestionService.updateQuestion(this, Question(changeQuestionId!!, text)),
            R.string.msg_question_edit_success,
            R.string.msg_question_edit_error
        )
        adapter.refreshAll()
    }

    override fun onDeleteConfirmed() {
        performServiceAction(
            QuestionService.deleteQuestion(this, changeQuestionId!!),
            R.string.msg_question_delete_success,
            R.string.msg_question_delete_error
        )
        adapter.refreshAll()
        // also clear the log
        if (QuestionService.hasAnswers(this, changeQuestionId!!)) {
            val dialogFragment = QuestionClearLogDialogFragment()
            dialogFragment.show(supportFragmentManager, QuestionClearLogDialogFragment::class.java.simpleName)
        }
    }

    override fun onHideConfirmed() {
        performServiceAction(
            QuestionService.hideQuestion(this, changeQuestionId!!),
            R.string.msg_question_hide_success,
            R.string.msg_question_hide_error
        )
        adapter.refreshAll()
    }

    override fun onClearLogByQuestionConfirmed() {
        AnswerStore.deleteByQuestionId(this, changeQuestionId!!)
        Snackbar.make(questionsActivityLayout, R.string.msg_clear_log_by_question_success, Snackbar.LENGTH_LONG).show()
    }

    private fun performServiceAction(success: Boolean, successMessageId: Int, errorMessageId: Int) =
        if (success) Snackbar.make(questionsActivityLayout, successMessageId, Snackbar.LENGTH_LONG).show()
        else Snackbar.make(questionsActivityLayout, errorMessageId, Snackbar.LENGTH_LONG).show()
}
