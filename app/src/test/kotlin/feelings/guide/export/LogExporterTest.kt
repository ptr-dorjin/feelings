package feelings.guide.export

import android.content.Context
import com.google.common.truth.Truth.assertThat
import feelings.guide.R
import feelings.guide.answer.AnswerForExport
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.doReturn
import java.time.LocalDateTime
import java.io.ByteArrayOutputStream

class LogExporterTest {

    @Test
    fun export_printsProperString() {
        // Given
        val outputStream = ByteArrayOutputStream()
        val answers = listOf(
                AnswerForExport(LocalDateTime.of(2020, 11, 14, 21, 45, 16),
                        "What do I feel right now?", "Disappointment, Abandonment"),
                AnswerForExport(LocalDateTime.of(2020, 11, 14, 20, 0, 0),
                        "What can I do for other people?", "Implement export feature")
        )
        val context = mock<Context> {
            on { getString(R.string.export_header_date_time) } doReturn "Date and time"
            on { getString(R.string.export_header_question) } doReturn "Question"
            on { getString(R.string.export_header_answer) } doReturn "Answer"
        }

        // When
        LogExporter().export(answers, outputStream, context)

        // Then
        val csv = outputStream.toString()
        assertThat(csv).isEqualTo(
                """"Date and time","Question","Answer"
                  |"2020-11-14 21:45:16","What do I feel right now?","Disappointment, Abandonment"
                  |"2020-11-14 20:00:00","What can I do for other people?","Implement export feature"
                  |""".trimMargin()
        )
    }
}