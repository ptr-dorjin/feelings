package feelings.guide

import kotlin.random.Random.Default.nextInt

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomAlphanumericString(stringLength: Int = 5): String =
    (1..stringLength)
        .map { nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")