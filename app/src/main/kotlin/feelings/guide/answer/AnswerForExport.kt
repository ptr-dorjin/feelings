package feelings.guide.answer

import feelings.guide.util.EXPORT_CONTENT_FORMATTER
import org.threeten.bp.LocalDateTime

data class AnswerForExport(
        val dateTime: LocalDateTime,
        val questionText: String,
        var answerText: String
) {

    fun toCsvLine(): Array<String> = arrayOf(
            dateTime.format(EXPORT_CONTENT_FORMATTER),
            questionText,
            answerText
    )
}
