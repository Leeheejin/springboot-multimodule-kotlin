/*
 * FJ's Kotlin utilities
 *
 * Distributed under no licences and no warranty.
 * Use this software at your own risk.
 */
package com.github.fj.lib.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun utcNow(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

fun LocalDateTime.utcEpochSecond(): Long = this.toEpochSecond(ZoneOffset.UTC)

/**
 * Calculates hour differences between UTC and current system time Zone.
 */
fun getHourDiffToUtc(): Int = ZoneOffset.systemDefault().getHourDiff()

/**
 * Calculates hour differences between UTC and given Zone Id.
 */
fun ZoneId.getHourDiff(): Int = parseZoneOffset(rules.getOffset(utcNow()))[0]

/**
 * Calculates minute differences between UTC and current system time Zone.
 */
fun getMinuteDiffToUtc(): Int = ZoneOffset.systemDefault().getMinuteDiff()

/**
 * Calculates minute differences between UTC and given Zone Id.
 */
fun ZoneId.getMinuteDiff(): Int = parseZoneOffset(rules.getOffset(utcNow()))[1]

/**
 * Calculates minute differences between UTC and current system time Zone.
 */
fun getSecondDiffToUtc(): Int = ZoneOffset.systemDefault().getSecondDiff()

/**
 * Calculates minute differences between UTC and given Zone Id.
 */
fun ZoneId.getSecondDiff(): Int = parseZoneOffset(rules.getOffset(utcNow()))[2]

// This logic is working based on java.time.ZoneOffset#buildId(int) implementation.
private fun parseZoneOffset(zoneOffset: ZoneOffset): IntArray {
    return with(zoneOffset.toString().split(":")) {
        if (isEmpty()) {
            return@with IntArray(3) { _ -> 0 }
        }

        val sign = if (get(0).startsWith("-")) {
            -1
        } else {
            1
        }

        val hourDiff = get(0).let {
            val hourStr = if (it.startsWith("+") || it.startsWith("-")) {
                it.substring(1)
            } else {
                it.substring(0)
            }

            sign * hourStr.toInt()
        }

        val minuteDiff = sign * get(1).toInt()

        val secondDiff = if (size > 2) {
            sign * get(2).toInt()
        } else {
            0
        }

        return@with IntArray(3).apply {
            set(0, hourDiff)
            set(1, minuteDiff)
            set(2, secondDiff)
        }
    }
}

// Do not use this method after year 2038
fun utcLocalDateTimeOf(timestamp: Int): LocalDateTime =
        utcLocalDateTimeOf(timestamp.toLong())

fun utcLocalDateTimeOf(timestamp: Long): LocalDateTime = if (timestamp > Integer.MAX_VALUE) {
    LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
} else {
    LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC)
}

