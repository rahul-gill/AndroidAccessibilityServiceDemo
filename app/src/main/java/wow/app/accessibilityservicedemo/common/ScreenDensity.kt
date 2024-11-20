package wow.app.accessibilityservicedemo.common

import android.content.Context
import android.util.DisplayMetrics


class ScreenDensity(val bucket: String, private val density: Int) {
    val isKnownDensity: Boolean
        get() = bucket != UNKNOWN

    override fun toString(): String {
        return "$bucket ($density)"
    }

    companion object {
        private const val UNKNOWN = "unknown"

        private const val XHDPI_TO_LDPI = 0.25f
        private const val XHDPI_TO_MDPI = 0.5f
        private const val XHDPI_TO_HDPI = 0.75f

        private val LEVELS = LinkedHashMap<Int, String>().apply {
            put(DisplayMetrics.DENSITY_LOW, "ldpi")
            put(DisplayMetrics.DENSITY_MEDIUM, "mdpi")
            put(DisplayMetrics.DENSITY_HIGH, "hdpi")
            put(DisplayMetrics.DENSITY_XHIGH, "xhdpi")
            put(DisplayMetrics.DENSITY_XXHIGH, "xxhdpi")
            put(DisplayMetrics.DENSITY_XXXHIGH, "xxxhdpi")
        }

        fun get(context: Context): ScreenDensity {
            val density = context.resources.displayMetrics.densityDpi

            var bucket = UNKNOWN

            for ((key, value) in LEVELS) {
                bucket = value
                if (key > density) {
                    break
                }
            }

            return ScreenDensity(bucket, density)
        }

        val bestDensityBucketForDevice: String
            get() {
                val density = get(ApplicationSingletonDependencies.application)

                return if (density.isKnownDensity) {
                    density.bucket
                } else {
                    "xhdpi"
                }
            }

        fun xhdpiRelativeDensityScaleFactor(density: String): Float {
            return when (density) {
                "ldpi" -> XHDPI_TO_LDPI
                "mdpi" -> XHDPI_TO_MDPI
                "hdpi" -> XHDPI_TO_HDPI
                "xhdpi" -> 1f
                else -> throw IllegalStateException("Unsupported density: $density")
            }
        }
    }
}
