package feelings.guide.data

import java.time.LocalDateTime

data class QuestionWithStats(
    val question: QuestionEntity,
    val answerCount: Int,
    val lastAnsweredAt: LocalDateTime?,
)
