package wow.app.accessibilityservicedemo.logs

import android.content.Context

interface LogSection {
    val title: String
    fun getContent(context: Context): CharSequence
}