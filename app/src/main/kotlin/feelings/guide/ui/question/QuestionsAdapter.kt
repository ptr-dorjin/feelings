package feelings.guide.ui.question

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import feelings.guide.R
import feelings.guide.data.question.Question
import kotlinx.android.synthetic.main.question_card.view.*

internal class QuestionsAdapter(context: Context, private val questionClickCallback: (Long) -> Unit) :
        RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var questions = emptyList<Question>() // Cached copy of question

    internal inner class QuestionViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var questionId: Long = 0
        val questionText: TextView = itemView.questionTextOnCard
        val popupMenu: ImageButton = itemView.popupMenu

        init {
            itemView.setOnClickListener { questionClickCallback(questionId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val itemView = inflater.inflate(R.layout.question_card, parent, false)
        return QuestionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val current = questions[position]
        holder.questionId = current.id
        holder.questionText.text = current.text
        holder.popupMenu.setTag(R.id.tag_question_id, current.id)
        holder.popupMenu.setTag(R.id.tag_is_user, current.isUser)
    }

    internal fun setQuestions(questions: List<Question>) {
        this.questions = questions
        notifyDataSetChanged()
    }

    override fun getItemCount() = questions.size
}
