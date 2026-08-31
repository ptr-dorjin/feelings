package feelings.guide.util

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

val EXPORT_FILE_NAME_FORMATTER: DateTimeFormatter = ofPattern("yyyyMMdd-HHmmss")
val EXPORT_CONTENT_FORMATTER: DateTimeFormatter = ofPattern("yyyy-MM-dd HH:mm:ss")
