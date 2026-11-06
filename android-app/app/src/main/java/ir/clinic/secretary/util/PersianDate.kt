package ir.clinic.secretary.util

import java.util.Calendar
import java.util.Locale

/**
 * Minimal, dependency-free Gregorian -> Shamsi (Jalali) converter.
 * Algorithm: Roozbeh Pournader / jdf (public domain), valid for modern dates.
 */
data class PersianDate(val year: Int, val month: Int, val day: Int, val weekdayName: String) {

    /** "1403/05/12" (ASCII digits – good for storage and sorting) */
    fun format(): String = String.format(Locale.US, "%04d/%02d/%02d", year, month, day)

    /** "شنبه ۱۲ مرداد ۱۴۰۳" */
    fun longFormat(): String = "$weekdayName ${Fa.digits(day.toString())} ${MONTHS[month - 1]} ${Fa.digits(year.toString())}"

    companion object {
        private val MONTHS = arrayOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )
        // Index = Calendar.DAY_OF_WEEK % 7  (Saturday=7 -> 0, Sunday=1 -> 1 ... Friday=6 -> 6)
        private val WEEKDAYS = arrayOf(
            "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
        )

        fun today(): PersianDate = from(Calendar.getInstance())

        fun from(cal: Calendar): PersianDate {
            val gy = cal.get(Calendar.YEAR)
            val gm = cal.get(Calendar.MONTH) + 1
            val gd = cal.get(Calendar.DAY_OF_MONTH)

            val cum = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
            val gy2 = if (gm > 2) gy + 1 else gy
            var days = 355666 + 365 * gy + (gy2 + 3) / 4 - (gy2 + 99) / 100 +
                    (gy2 + 399) / 400 + gd + cum[gm - 1]

            var jy = -1595 + 33 * (days / 12053)
            days %= 12053
            jy += 4 * (days / 1461)
            days %= 1461
            if (days > 365) {
                jy += (days - 1) / 365
                days = (days - 1) % 365
            }
            val jm: Int
            val jd: Int
            if (days < 186) {
                jm = 1 + days / 31
                jd = 1 + days % 31
            } else {
                jm = 7 + (days - 186) / 30
                jd = 1 + (days - 186) % 30
            }
            return PersianDate(jy, jm, jd, WEEKDAYS[cal.get(Calendar.DAY_OF_WEEK) % 7])
        }
    }
}

/** Persian digit / number helpers. */
object Fa {
    private const val FA = "۰۱۲۳۴۵۶۷۸۹"

    /** "1403/05" -> "۱۴۰۳/۰۵" */
    fun digits(s: String): String = buildString {
        for (c in s) append(if (c in '0'..'9') FA[c - '0'] else c)
    }

    /** Converts Persian/Arabic digits typed by the user to ASCII digits. */
    fun normalize(s: String): String = buildString {
        for (c in s) append(
            when (c) {
                in '۰'..'۹' -> '0' + (c - '۰')
                in '٠'..'٩' -> '0' + (c - '٠')
                else -> c
            }
        )
    }

    /** Parses user input like "۱۵۰٬۰۰۰" or "150000" to Long, or null if empty. */
    fun parseNumber(s: String?): Long? =
        normalize(s.orEmpty()).filter { it.isDigit() }.toLongOrNull()

    /** 150000 -> "۱۵۰٬۰۰۰" */
    fun money(v: Long): String = digits(String.format(Locale.US, "%,d", v)).replace(',', '٬')
}
