package feelings.guide.answer

import feelings.guide.util.EXPORT_CONTENT_FORMATTER
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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

    override fun equals(other: Any?): Boolean {
        val o = other as AnswerForExport
        return dateTime.truncatedTo(ChronoUnit.SECONDS) == o.dateTime.truncatedTo(ChronoUnit.SECONDS)
                && questionText == o.questionText
                && answerText == o.answerText
    }

    override fun hashCode(): Int {
        var result = dateTime.truncatedTo(ChronoUnit.SECONDS).hashCode()
        result = 31 * result + questionText.hashCode()
        result = 31 * result + answerText.hashCode()
        return result
    }


}
