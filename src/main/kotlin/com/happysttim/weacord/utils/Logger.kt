package com.happysttim.weacord.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
object Logger {
    inline fun <reified T> getLogger(): Logger {
        return LoggerFactory.getLogger(T::class.java)
    }
}