package feelings.guide.ui.question

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns._ID
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import feelings.guide.R
import feelings.guide.question.COLUMN_IS_USER
import feelings.guide.question.COLUMN_TEXT
import feelings.guide.question.QuestionService
import feelings.guide.ui.RecyclerViewCursorAdapter
import kotlinx.android.synthetic.main.question_card.view.*

internal class QuestionsAdapter(context: Context, private val questionClickCallback: (Long) -> Unit) :
        RecyclerViewCursorAdapter<QuestionsAdapter.QuestionViewHolder>(context) {

    internal inner class QuestionViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var questionId: Long = 0
        val questionText: TextView = itemView.questionTextOnCard
        val popupMenu: ImageButton = itemView.popupMenu

        init {
            itemView.setOnClickListener { questionClickCallback(questionId) }
        }
    }

    init {
        refreshAll()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.question_card, parent, false)
        return QuestionViewHolder(view)
    }

    public override fun onBindViewHolder(holder: QuestionViewHolder, cursor: Cursor) {
        val questionId = cursor.getLong(cursor.getColumnIndexOrThrow(_ID))
        val isUser = 1 == cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER))
        holder.let {
            it.questionId = questionId
            it.questionText.text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT))
            it.popupMenu.setTag(R.id.tag_question_id, questionId)
            it.popupMenu.setTag(R.id.tag_is_user, isUser)
        }
    }

    fun refreshAll() {
        swapCursor(QuestionService.getAllQuestions(context))
    }
}
