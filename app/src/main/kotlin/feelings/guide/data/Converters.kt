package feelings.guide.data

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Matches the legacy SQLite storage format exactly, so migrated rows parse unchanged. */
val DB_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

class Converters {
    @TypeConverter
    fun fromDbString(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it, DB_DATE_TIME_FORMATTER) }

    @TypeConverter
    fun toDbString(dateTime: LocalDateTime?): String? = dateTime?.format(DB_DATE_TIME_FORMATTER)
}
