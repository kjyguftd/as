package io.github.junkfood.heal.util

import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TextUtil {

    private const val TAG = "TextUtil"
    private const val xmlDatePattern = "EEE MMM dd HH:mm:ss zzz yyyy"
    private const val opmlDatePattern = "dd MMM yy HH:mm:ss Z"
    private const val hhmmss = "%2d:%02d:%02d"
    private const val mmss = "%02d:%02d"
    private const val SECOND = 1
    private const val MINUTE = 60
    private const val HOUR = 3_600
    private val durationRegex = Regex("""((\d+):)?(\d{2}):(\d{2})""")

    fun formatDate(date: Date): String {
        val calender = Calendar.getInstance()
        calender.time = date
        return calender.time.toString()
    }

    fun parseXmlStringToDate(string: String): Date? {
        val simpleDateFormat = SimpleDateFormat(xmlDatePattern, Locale.US)
        return simpleDateFormat.parse(string)
    }

    fun getOpmlStyleDate(): String {
        val simpleDateFormat = SimpleDateFormat(opmlDatePattern, Locale.US)
        return simpleDateFormat.format(Date())
    }

    fun parseDate(date: Date): String {
        val df: DateFormat =
            DateFormat.getDateInstance(
                DateFormat.MEDIUM,
                java.util.Locale.getDefault()
            )
        return df.format(date)
    }

    fun compareDate(s1: String, s2: String): Int {
        if (s1 == s2) return 0
        return if (parseXmlStringToDate(s1)?.after(parseXmlStringToDate(s2)) == true) 1 else -1
    }

    fun durationToText(duration: Long): String {
        val sec = duration / 1000
        return if (sec > 3600)
            hhmmss.format(sec / 3600, sec % 3600 / 60, sec % 60)
        else mmss.format(sec % 3600 / 60, sec % 60)
    }

    fun String.trimDescription(length: Int): String {
        return this.substring(0, minOf(this.length, length)).replace(Regex("\\n+"), "\n")
    }

    /** Duration of the episode, in one of the following formats:
     *
     * `hours:minutes:seconds`
     *
     * `minutes:seconds`
     *
     * `total_seconds`
     *
     * [Reference](https://support.google.com/podcast-publishers/answer/9889544?hl=en)
     */
    fun parseTextToDuration(durationText: String): Long {
        //
        if (!durationText.contains(':')) return durationText.toLong() * 1000L
        val matchResult = durationRegex.find(durationText, 0)
        return if (matchResult != null) {
            val groups = matchResult.groups
            if (groups.count() == 5) {
                val hour = groups[2]?.value?.toLong() ?: 0
                val minute = groups[3]?.value?.toLong() ?: 0
                val second = groups[4]?.value?.toLong() ?: 0
                (hour * HOUR + minute * MINUTE + second * SECOND) * 1000L // To milliseconds
            } else {
                Log.d(TAG, "parseTextToDuration: parse failed")
                0L
            }
        } else {
            Log.d(TAG, "parseTextToDuration: Illegal durationText")
            0L
        }
    }

}