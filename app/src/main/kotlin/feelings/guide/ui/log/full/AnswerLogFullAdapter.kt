package feelings.guide.ui.log.full

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
 * - full answer log (for all questions)
 */
internal class AnswerLogFullAdapter(context: Context) :
    RecyclerViewCursorAdapter<AnswerLogFullAdapter.AnswerLogHolder>(context) {

    private val dateTimeFormat: String = DateTimeUtil.getDateTimeFormat(context)

    internal class AnswerLogHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTimeView: TextView = itemView.logFullDateTime
        val questionView: TextView = itemView.logFullQuestionText
        val answerView: TextView = itemView.logFullAnswer
    }

    init {
        refresh()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerLogHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.answer_log_full_item, parent, false)
        return AnswerLogHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerLogHolder, cursor: Cursor) {
        val (questionId1, dateTime, answerText) = AnswerStore.mapFromCursor(cursor)

        holder.dateTimeView.text = dateTime.format(ofPattern(dateTimeFormat))
        holder.questionView.text = QuestionService.getQuestionText(context, questionId1)
        holder.answerView.text = answerText
    }

    fun refresh() {
        val cursor = AnswerStore.getAnswers(context, -1)
        swapCursor(cursor)
    }

    fun getByPosition(position: Int): Answer {
        val cursor = cursor!!
        cursor.moveToPosition(position)
        return AnswerStore.mapFromCursor(cursor)
    }

    fun getPositionById(answerId: Long): Int {
        val cursor = cursor!!
        while (cursor.moveToNext()) {
            if (answerId == cursor.getLong(cursor.getColumnIndexOrThrow(_ID))) {
                return cursor.position
            }
        }
        return -1
    }
}
