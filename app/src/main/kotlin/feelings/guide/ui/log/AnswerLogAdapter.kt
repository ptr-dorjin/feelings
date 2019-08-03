package feelings.guide.ui.log

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns._ID
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import feelings.guide.R
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.question.QuestionService
import feelings.guide.ui.RecyclerViewCursorAdapter
import feelings.guide.util.DateTimeUtil
import kotlinx.android.synthetic.main.answer_log_full_item.view.*
import org.threeten.bp.format.DateTimeFormatter.ofPattern

/**
 * Used on
 * - answer log by one question
 * - full answer log (for all questions)
 */
internal class AnswerLogAdapter(context: Context, private val isFull: Boolean, private val questionId: Long) :
    RecyclerViewCursorAdapter<AnswerLogAdapter.AnswerLogHolder>(context) {

    private val dateTimeFormat: String = DateTimeUtil.getDateTimeFormat(context)

    internal class AnswerLogHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTimeView: TextView = itemView.answerLogDateTime
        val questionView: TextView? = itemView.questionTextInFullLog
        val answerView: TextView = itemView.answerLogAnswer
    }

    init {
        refresh()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerLogHolder {
        val itemViewId = when {
            isFull -> R.layout.answer_log_full_item
            else -> R.layout.answer_log_by_question_item
        }
        val view = LayoutInflater.from(context).inflate(itemViewId, parent, false)
        return AnswerLogHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerLogHolder, cursor: Cursor) {
        val (questionId1, dateTime, answerText) = AnswerStore.mapFromCursor(cursor)

        holder.dateTimeView.text = dateTime.format(ofPattern(dateTimeFormat))
        if (holder.questionView != null) {
            //in case of full log
            holder.questionView.text = QuestionService.getQuestionText(context, questionId1)
        }
        holder.answerView.text = answerText
    }

    fun refresh() {
        val cursor = AnswerStore.getAnswers(context, if (isFull) -1 else questionId)
        swapCursor(cursor)
    }

    fun getByPosition(position: Int): Answer {
        cursor!!.moveToPosition(position)
        return AnswerStore.mapFromCursor(cursor!!)
    }

    fun getPositionById(answerId: Long): Int {
        while (cursor!!.moveToNext()) {
            if (answerId == cursor!!.getLong(cursor!!.getColumnIndexOrThrow(_ID))) {
                return cursor!!.position
            }
        }
        return -1
    }
}
