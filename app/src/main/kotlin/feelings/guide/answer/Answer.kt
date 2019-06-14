package feelings.guide.answer

import org.threeten.bp.LocalDateTime

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
}
