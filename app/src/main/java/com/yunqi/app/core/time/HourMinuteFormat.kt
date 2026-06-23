package com.yunqi.app.core.time

import java.time.LocalTime

/**
 * Parses the app's strict reminder time format. Only zero-padded HH:mm is accepted.
 */
fun String.toStrictHourMinuteOrNull(): LocalTime? {
    val value = trim()
    if (!value.isStrictHourMinute()) return null
    return LocalTime.of(
        value.substring(0, 2).toInt(),
        value.substring(3, 5).toInt(),
    )
}

fun String.isStrictHourMinute(): Boolean {
    val value = trim()
    return value.length == 5 && value[2] == ':' &&
        value[0].isDigit() && value[1].isDigit() &&
        value[3].isDigit() && value[4].isDigit() &&
        value.substring(0, 2).toInt() in 0..23 &&
        value.substring(3, 5).toInt() in 0..59
}
