package feelings.guide.feelings

import android.content.Context
import feelings.guide.R

data class FeelingsGroup(
    val label: String,
    val words: List<String>,
) {
    val count: Int get() = words.size
}

private data class FeelingsGroupResources(val labelResId: Int, val arrayResId: Int)

private val GROUP_RESOURCES = listOf(
    FeelingsGroupResources(R.string.anger, R.array.anger_array),
    FeelingsGroupResources(R.string.fear, R.array.fear_array),
    FeelingsGroupResources(R.string.sadness, R.array.sadness_array),
    FeelingsGroupResources(R.string.joy, R.array.joy_array),
    FeelingsGroupResources(R.string.love, R.array.love_array),
    FeelingsGroupResources(R.string.feelings_group_other, R.array.other_array),
)

fun loadFeelingsGroups(context: Context): List<FeelingsGroup> = GROUP_RESOURCES.map { resources ->
    FeelingsGroup(
        label = context.getString(resources.labelResId),
        words = context.resources.getStringArray(resources.arrayResId).toList(),
    )
}
