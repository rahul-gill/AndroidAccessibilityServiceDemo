package wow.app.accessibilityservicedemo.logs

import android.content.Context
import java.util.Collections

class LogSectionThreads : LogSection {
    override val title: String = "THREADS"

    override fun getContent(context: Context): CharSequence {
        val builder = StringBuilder()

        val threads: List<Thread> = ArrayList(Thread.getAllStackTraces().keys)
        Collections.sort(threads, Comparator.comparingLong(Thread::getId))

        for (thread in threads) {
            builder.append("[").append(thread.id).append("] ").append(thread.name).append("\n")
        }

        return builder
    }
}

