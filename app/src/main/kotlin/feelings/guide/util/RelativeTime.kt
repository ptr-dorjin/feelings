package feelings.guide.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import feelings.guide.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun relativeTimeLabel(dateTime: LocalDateTime): String {
    val days = ChronoUnit.DAYS.between(dateTime.toLocalDate(), LocalDate.now())
    return when {
        days <= 0 -> stringResource(R.string.relative_today)
        days == 1L -> stringResource(R.string.relative_yesterday)
        else -> pluralStringResource(R.plurals.relative_days_ago, days.toInt(), days.toInt())
    }
}
