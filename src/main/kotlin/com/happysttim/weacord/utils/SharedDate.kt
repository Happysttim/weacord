package com.happysttim.weacord.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SharedDate {

    private var date = LocalDateTime.now()

    fun update() {
        date = LocalDateTime.now()
    }

    fun getDate(): Long {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString().toLong()
    }

    fun getDateOnTime(): Long {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd0000")).toString().toLong()
    }

    fun compare(time: LocalDateTime): Boolean {
        return date.year == time.year && date.month == time.month && date.dayOfMonth == time.dayOfMonth
    }

}