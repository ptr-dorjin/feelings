package feelings.guide.export

import android.content.Context
import com.opencsv.CSVWriter
import feelings.guide.R
import feelings.guide.answer.AnswerForExport
import java.io.OutputStream
import java.io.OutputStreamWriter


internal class LogExporter {
    /**
     * Closes outputStream at the end
     */
    internal fun export(answers: List<AnswerForExport>,
                           outputStream: OutputStream,
                           context: Context) {
        CSVWriter(OutputStreamWriter(outputStream)).use { csvWriter ->
            csvWriter.writeNext(getHeader(context))
            for (answer in answers) {
                csvWriter.writeNext(answer.toCsvLine())
            }
        }
    }

    private fun getHeader(context: Context): Array<String> = arrayOf(
            context.getString(R.string.export_header_date_time),
            context.getString(R.string.export_header_question),
            context.getString(R.string.export_header_answer)
    )
}