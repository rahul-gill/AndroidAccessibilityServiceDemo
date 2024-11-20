package wow.app.accessibilityservicedemo.logs

import android.content.Context

internal class LogSectionConstraints : LogSection {
    override val title: String
        get() = "CONSTRAINTS"

    override fun getContent(context: Context): CharSequence {
        val output = StringBuilder()
        val factories: Map<String, Constraint.Factory> =
            JobManagerFactories.getConstraintFactories(ApplicationDependencies.getApplication())
        val keyLength: Int = factories.keys.maxOfOrNull { it.length } ?: 0

        for ((key, value) in factories) {
            output.append(rightPad(key, keyLength)).append(": ").append(value.create().isMet())
                .append("\n")
        }

        return output
    }

    private fun rightPad(value: String, length: Int): String {
        return if (value.length >= length) {
            value
        } else {
            val out = StringBuilder(value)
            repeat(length - value.length) { out.append(" ") }
            out.toString()
        }
    }

}
