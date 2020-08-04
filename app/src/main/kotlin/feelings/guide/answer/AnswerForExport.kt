package feelings.guide.answer

import org.threeten.bp.LocalDateTime

data class AnswerForExport(
        val dateTime: LocalDateTime,
        val questionText: String,
        var answerText: String
)
