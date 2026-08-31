package feelings.guide.ui.nav

object Routes {
    const val QUESTIONS = "questions"
    const val ANSWER = "answer/{questionId}?answerId={answerId}"
    const val LOG_FULL = "log/full"
    const val LOG_QUESTION = "log/question/{questionId}"
    const val SETTINGS = "settings"

    fun answer(questionId: Long, answerId: Long? = null) =
        "answer/$questionId?answerId=${answerId ?: -1}"

    fun logQuestion(questionId: Long) = "log/question/$questionId"
}
