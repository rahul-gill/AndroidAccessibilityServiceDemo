package wow.app.accessibilityservicedemo.logs

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import wow.app.accessibilityservicedemo.BuildConfig

class LogSectionPermissions : LogSection {
    override val title: String = "PERMISSIONS"

    override fun getContent(context: Context): CharSequence {
        val out = StringBuilder()
        val status: MutableList<Pair<String, Boolean>> = ArrayList()

        try {
            val info: PackageInfo = context.packageManager.getPackageInfo(
                BuildConfig.APPLICATION_ID,
                PackageManager.GET_PERMISSIONS
            )

            for (i in info.requestedPermissions.indices) {
                val next = Pair(
                    info.requestedPermissions[i],
                    (info.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0
                )
                status.add(next)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return "Unable to retrieve."
        }

        status.sortBy { it.first }

        for (pair in status) {
            out.append(pair.first).append(": ")
                .append(if (pair.second) "YES" else "NO")
                .append("\n")
        }

        return out
    }
}
