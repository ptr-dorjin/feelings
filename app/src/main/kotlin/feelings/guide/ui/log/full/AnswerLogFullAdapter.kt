package feelings.guide.ui.log.full

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns._ID
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import feelings.guide.answer.Answer
import feelings.guide.answer.AnswerStore
import feelings.guide.databinding.AnswerLogFullItemBinding
import feelings.guide.question.QuestionService
import feelings.guide.ui.RecyclerViewCursorAdapter
import feelings.guide.util.DateTimeUtil
import java.time.format.DateTimeFormatter.ofPattern

/**
 * Used on
 * - full answer log (for all questions)
 */
internal class AnswerLogFullAdapter(context: Context) :
    RecyclerViewCursorAdapter<AnswerLogFullAdapter.AnswerLogHolder>(context) {

    private val dateTimeFormat: String = DateTimeUtil.getDateTimeFormat(context)

    internal class AnswerLogHolder(itemBinding: AnswerLogFullItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val dateTimeView: TextView = itemBinding.logFullDateTime
        val questionView: TextView = itemBinding.logFullQuestionText
        val answerView: TextView = itemBinding.logFullAnswer
    }

    init {
        refresh()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerLogHolder {
        val itemBinding = AnswerLogFullItemBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return AnswerLogHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: AnswerLogHolder, cursor: Cursor) {
        val (questionId, dateTime, answerText) = AnswerStore.mapFromCursor(cursor)

        holder.dateTimeView.text = dateTime.format(ofPattern(dateTimeFormat))
        holder.questionView.text = QuestionService.getQuestionText(context, questionId)
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
