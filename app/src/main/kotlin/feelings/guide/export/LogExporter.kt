package feelings.guide.export

import com.opencsv.CSVWriter
import feelings.guide.answer.AnswerForExport
import java.io.OutputStream
import java.io.OutputStreamWriter


/**
 * Closes outputStream at the end
 */
internal fun exportLog(answers: List<AnswerForExport>, outputStream: OutputStream) {
    CSVWriter(OutputStreamWriter(outputStream)).use { csvWriter ->
        csvWriter.writeNext(arrayOf("dateTime", "question", "answer"))
        for (answer in answers) {
            csvWriter.writeNext(answer.toCsvLine())
        }
    }
}
