package wow.app.accessibilityservicedemo.common

import android.content.Intent
import android.os.Bundle


fun Intent.addHighlightEntryExtras(string: String): Intent {
    putExtra(EXTRA_FRAGMENT_ARG_KEY, string)
    putExtra(
        EXTRA_SHOW_FRAGMENT_ARGUMENTS,
        Bundle().apply { putString(EXTRA_FRAGMENT_ARG_KEY, string) }
    )
    return this
}


private const val EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key"
private const val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args"
