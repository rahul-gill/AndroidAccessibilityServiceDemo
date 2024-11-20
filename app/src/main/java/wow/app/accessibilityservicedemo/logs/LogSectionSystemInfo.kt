package wow.app.accessibilityservicedemo.logs

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import wow.app.accessibilityservicedemo.common.ScreenDensity
import wow.app.accessibilityservicedemo.common.getAnimationScale
import java.util.Locale


class LogSectionSystemInfo : LogSection {
    override val title: String
        get() = "SYSINFO"

    override fun getContent(context: Context): CharSequence {
        val pm = context.packageManager
        val builder = StringBuilder()

        builder.append("Time              : ").append(System.currentTimeMillis()).append('\n')
        builder.append("Manufacturer      : ").append(Build.MANUFACTURER).append("\n")
        builder.append("Model             : ").append(Build.MODEL).append("\n")
        builder.append("Product           : ").append(Build.PRODUCT).append("\n")
        builder.append("Screen            : ").append(getScreenResolution(context)).append(", ")
            .append(ScreenDensity.get(context)).append(", ")
            .append(getScreenRefreshRate(context)).append("\n")
        builder.append("Font Scale        : ").append(context.resources.configuration.fontScale)
            .append("\n")
        builder.append("Animation Scale   : ").append(getAnimationScale(context))
            .append("\n")
        builder.append("Android           : ").append(Build.VERSION.RELEASE).append(", API ")
            .append(Build.VERSION.SDK_INT).append(" (")
            .append(Build.VERSION.INCREMENTAL).append(", ")
            .append(Build.DISPLAY).append(")\n")
        builder.append("ABIs              : ").append(
            TextUtils.join(
                ", ",
                supportedAbis
            )
        ).append("\n")
        builder.append("Memory            : ").append(memoryUsage).append("\n")
        builder.append("Memclass          : ").append(getMemoryClass(context)).append("\n")
        builder.append("MemInfo           : ").append(getMemoryInfo(context)).append("\n")
        builder.append("OS Host           : ").append(Build.HOST).append("\n")
        builder.append("RecipientId       : ").append(
            if (SignalStore.registrationValues().isRegistrationComplete()) Recipient.self()
                .getId() else "N/A"
        ).append("\n")
        builder.append("ACI               : ").append(getCensoredAci(context)).append("\n")
        builder.append("Device ID         : ").append(SignalStore.account().getDeviceId())
            .append("\n")
        builder.append("Censored          : ")
            .append(ApplicationDependencies.getSignalServiceNetworkAccess().isCensored())
            .append("\n")
        builder.append("Network Status    : ").append(NetworkUtil.getNetworkStatus(context))
            .append("\n")
        builder.append("Data Saver        : ").append(DeviceProperties.getDataSaverState(context))
            .append("\n")
        builder.append("Play Services     : ").append(getPlayServicesString(context)).append("\n")
        builder.append("FCM               : ").append(SignalStore.account().isFcmEnabled())
            .append("\n")
        builder.append("BkgRestricted     : ")
            .append(DeviceProperties.isBackgroundRestricted(context))
            .append("\n")
        builder.append("Locale            : ").append(Locale.getDefault()).append("\n")
        builder.append("Linked Devices    : ").append(TextSecurePreferences.isMultiDevice(context))
            .append("\n")
        builder.append("First Version     : ")
            .append(TextSecurePreferences.getFirstInstallVersion(context)).append("\n")
        builder.append("Days Installed    : ")
            .append(VersionTracker.getDaysSinceFirstInstalled(context)).append("\n")
        builder.append("Build Variant     : ").append(BuildConfig.BUILD_DISTRIBUTION_TYPE)
            .append(BuildConfig.BUILD_ENVIRONMENT_TYPE).append(BuildConfig.BUILD_VARIANT_TYPE)
            .append("\n")
        builder.append("Emoji Version     : ").append(getEmojiVersionString(context)).append("\n")
        builder.append("RenderBigEmoji    : ").append(FontUtil.canRenderEmojiAtFontSize(1024))
            .append("\n")
        builder.append("DontKeepActivities: ").append(getDontKeepActivities(context)).append("\n")
        builder.append("Server Time Offset: ")
            .append(SignalStore.misc().getLastKnownServerTimeOffset()).append(" ms (last updated: ")
            .append(SignalStore.misc().getLastKnownServerTimeOffsetUpdateTime()).append(")")
            .append("\n")
        builder.append("Telecom           : ").append(AndroidTelecomUtil.getTelecomSupported())
            .append("\n")
        builder.append("User-Agent        : ").append(StandardUserAgentInterceptor.USER_AGENT)
            .append("\n")
        builder.append("SlowNotifications : ")
            .append(SlowNotificationHeuristics.isHavingDelayedNotifications()).append("\n")
        builder.append("PotentiallyBattery: ")
            .append(SlowNotificationHeuristics.isPotentiallyCausedByBatteryOptimizations())
            .append("\n")
        if (BuildConfig.MANAGES_APP_UPDATES) {
            builder.append("ApkManifestUrl    : ").append(BuildConfig.APK_UPDATE_MANIFEST_URL)
                .append("\n")
        }
        builder.append("App               : ")

        try {
            builder.append(pm.getApplicationLabel(pm.getApplicationInfo(context.packageName, 0)))
                .append(" ")
                .append(pm.getPackageInfo(context.packageName, 0).versionName)
                .append(" (")
                .append(BuildConfig.CANONICAL_VERSION_CODE)
                .append(", ")
                .append(Util.getManifestApkVersion(context))
                .append(") (")
                .append(BuildConfig.GIT_HASH).append(") \n")
        } catch (nnfe: PackageManager.NameNotFoundException) {
            builder.append("Unknown\n")
        }
        builder.append("Package           : ").append(BuildConfig.APPLICATION_ID).append(" (")
            .append(
                getSigningString(context)
            ).append(")")

        return builder
    }

    companion object {
        private val memoryUsage: String
            get() {
                val info = Runtime.getRuntime()
                val totalMemory = info.totalMemory()

                return java.lang.String.format(
                    Locale.ENGLISH,
                    "%dM (%.2f%% free, %dM max)",
                    ByteUnit.BYTES.toMegabytes(totalMemory),
                    info.freeMemory().toFloat() / totalMemory * 100f,
                    ByteUnit.BYTES.toMegabytes(info.maxMemory())
                )
            }

        private fun getMemoryClass(context: Context): String {
            val activityManager: ActivityManager = ServiceUtil.getActivityManager(context)
            var lowMem = ""

            if (activityManager.isLowRamDevice) {
                lowMem = ", low-mem device"
            }

            return activityManager.memoryClass.toString() + lowMem
        }

        private fun getMemoryInfo(context: Context): String {
            val info: ActivityManager.MemoryInfo = DeviceProperties.getMemoryInfo(context)
            return java.lang.String.format(
                Locale.US,
                "availMem: %d mb, totalMem: %d mb, threshold: %d mb, lowMemory: %b",
                ByteUnit.BYTES.toMegabytes(info.availMem),
                ByteUnit.BYTES.toMegabytes(info.totalMem),
                ByteUnit.BYTES.toMegabytes(info.threshold),
                info.lowMemory
            )
        }

        private val supportedAbis: Iterable<String>
            get() = listOf(*Build.SUPPORTED_ABIS)


        private fun getScreenResolution(context: Context): String {
            val displayMetrics = DisplayMetrics()
            val windowManager: WindowManager = ServiceUtil.getWindowManager(context)

            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels.toString() + "x" + displayMetrics.heightPixels
        }

        private fun getScreenRefreshRate(context: Context): String {
            return java.lang.String.format(
                Locale.ENGLISH,
                "%.2f hz",
                ServiceUtil.getWindowManager(context).getDefaultDisplay().getRefreshRate()
            )
        }


        private fun getDontKeepActivities(context: Context): String {
            val setting = Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ALWAYS_FINISH_ACTIVITIES,
                0
            )
            return if (setting == 0) "false" else "true"
        }
    }
}
