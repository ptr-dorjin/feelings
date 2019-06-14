package feelings.guide.question

data class Question(
    var id: Long = 0,
    /**
     * only for built-in questions
     */
    var code: String? = null,
    var text: String? = null,
    /**
     * only for built-in questions
     */
    var description: String? = null,
    var isUser: Boolean = true,
    /**
     * can be set for user questions - by user, for built-in questions - only during app update
     */
    var isDeleted: Boolean = false,
    /**
     * only for built-in questions. can be done by user
     */
    var isHidden: Boolean = false
) {
    constructor(text: String) : this() {
        this.text = text
        isUser = true
    }

    constructor(id: Long, text: String) : this() {
        this.id = id
        this.text = text
        isUser = true
    }
}