package wow.app.accessibilityservicedemo.logs

import android.content.Context
import wow.app.accessibilityservicedemo.common.MemoryTracker


class LogSectionMemory : LogSection {
    override val title: String = "MEMORY"

    override fun getContent(context: Context): CharSequence {
        val appMemory = MemoryTracker.getAppJvmHeapUsage()
        val nativeMemory = MemoryTracker.getSystemNativeMemoryUsage(context)
        var base = """
      -- App JVM Heap
      Used         : ${appMemory.usedBytes.byteDisplay()}
      Free         : ${appMemory.freeBytes.byteDisplay()}
      Current Total: ${appMemory.currentTotalBytes.byteDisplay()}
      Max Possible : ${appMemory.maxPossibleBytes.byteDisplay()}
      
      -- System Native Memory
      Used                : ${nativeMemory.usedBytes.byteDisplay()}
      Free                : ${nativeMemory.freeBytes.byteDisplay()}
      Total               : ${nativeMemory.totalBytes.byteDisplay()}
      Low Memory Threshold: ${nativeMemory.lowMemoryThreshold.byteDisplay()}
      Low Memory?         : ${nativeMemory.lowMemory}
    """.trimIndent()

        val detailedMemory = MemoryTracker.getDetailedMemoryStats()

        base += "\n\n"
        base += """
    -- Detailed Memory (API 23+)
    App JVM Heap Usage   : ${detailedMemory.appJavaHeapUsageKb?.kbDisplay()}
    App Native Heap Usage: ${detailedMemory.appNativeHeapUsageKb?.kbDisplay()}
    Code Usage           : ${detailedMemory.codeUsageKb?.kbDisplay()}
    Graphics Usage       : ${detailedMemory.graphicsUsageKb?.kbDisplay()}
    Stack Usage          : ${detailedMemory.stackUsageKb?.kbDisplay()}
    Other Usage          : ${detailedMemory.appOtherUsageKb?.kbDisplay()}
  """.trimIndent()

        return base
    }


    private fun Long.byteDisplay(): String {
        val bytesToMB = this / (1024 * 1024f)
        return "$this bytes (%.2f MiB)".format(bytesToMB)
    }

    private fun Long.kbDisplay(): String {
        val bytesToMB = this / (1024 * 1024f)
        return "$this KiB (%.2f MiB)".format(bytesToMB)
    }

}
