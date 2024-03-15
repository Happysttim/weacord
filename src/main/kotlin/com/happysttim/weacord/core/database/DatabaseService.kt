package com.happysttim.weacord.core.database

import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

open class DatabaseService protected constructor() {

    protected open var database: Connection? = null
    protected open var statement: Statement? = null

    companion object {
        private var instance: DatabaseService? = null

        fun getInstance(): DatabaseService {
            return instance ?: synchronized(this) {
                DatabaseService().also {
                    instance = it
                }
            }
        }
    }

    fun start() {
        database = try {
            DriverManager.getConnection("jdbc:sqlite:database.db")
        } catch(e: IOException) {
            println(e.stackTrace)
            null
        }

        database?.let {
            statement = database!!.createStatement()
        }

        statement?.apply {
            queryTimeout = 30
        }
    }

    fun stop(callback: () -> Unit) {
        database?.run {
            if(!isClosed)
                close()
        }

        statement?.run {
            if(!isClosed)
                close()
        }

        callback()
    }

    fun executeQuery(sql: String): ResultSet? {
        return statement?.executeQuery(sql)
    }

    fun execute(sql: String): Boolean? {
        return statement?.execute(sql)
    }

    fun isConnected(): Boolean = (database == null || !database!!.isClosed) && (statement == null || !statement!!.isClosed)
}