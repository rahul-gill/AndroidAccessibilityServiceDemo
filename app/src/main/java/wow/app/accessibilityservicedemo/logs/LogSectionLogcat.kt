package wow.app.accessibilityservicedemo.logs

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class LogSectionLogcat : LogSection {
    override val title: String
        get() = "LOGCAT"

    override fun getContent(context: Context): CharSequence {
        try {
            val process = Runtime.getRuntime().exec("logcat -d")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            val log = StringBuilder()
            val separator = System.lineSeparator()

            bufferedReader.forEachLine { line ->
                log.append(line).append(separator)
            }
            return log.toString()
        } catch (ioe: IOException) {
            return "Failed to retrieve logcat content."
        }
    }
}