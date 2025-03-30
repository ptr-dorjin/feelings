package feelings.guide.answer

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class Answer(
        val questionId: Long,
        val dateTime: LocalDateTime,
        var answerText: String?
) {
    var id: Long = 0

    /**
     * Ctor for cases when ID is known beforehand. Ex: restoring deleted answer
     */
    internal constructor(id: Long, questionId: Long, dateTime: LocalDateTime, answerText: String)
            : this(questionId, dateTime, answerText) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        val o = other as Answer
        return questionId == o.questionId
                && dateTime.truncatedTo(ChronoUnit.MILLIS) == o.dateTime.truncatedTo(ChronoUnit.MILLIS)
                && answerText == o.answerText
    }

    override fun hashCode(): Int {
        var result = questionId.hashCode()
        result = 31 * result + dateTime.truncatedTo(ChronoUnit.MILLIS).hashCode()
        result = 31 * result + (answerText?.hashCode() ?: 0)
        return result
    }
}
