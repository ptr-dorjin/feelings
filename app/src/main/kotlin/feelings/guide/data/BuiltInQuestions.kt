package feelings.guide.data

import feelings.guide.R

data class BuiltInQuestion(
    val code: String,
    val textResId: Int,
)

/** Order matches the legacy DB population order — the feelings question must stay first (id 1). */
val BUILT_IN_QUESTIONS: List<BuiltInQuestion> = listOf(
    BuiltInQuestion(CODE_FEELINGS, R.string.q_text_feelings),
    BuiltInQuestion("q_gratitude", R.string.q_text_gratitude),
    BuiltInQuestion("q_do_body", R.string.q_text_do_body),
    BuiltInQuestion("q_do_mind", R.string.q_text_do_mind),
    BuiltInQuestion("q_do_close", R.string.q_text_do_close),
    BuiltInQuestion("q_do_others", R.string.q_text_do_others),
)
