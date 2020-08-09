package feelings.guide.answer

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

data class AnswerForExport(
        val dateTime: LocalDateTime,
        val questionText: String,
        var answerText: String
) {

    fun toCsvLine(): Array<String> = arrayOf(
        dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        questionText,
        answerText
    )
}
