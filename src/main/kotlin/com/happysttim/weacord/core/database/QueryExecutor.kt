package com.happysttim.weacord.core.database

import com.happysttim.weacord.core.database.querybuilder.QueryBuilder
import org.sqlite.SQLiteException
import java.sql.ResultSet

class QueryExecutor {

    private val dbService: DatabaseService = DatabaseService.getInstance()

    private fun builderToQuery(builder: QueryBuilder): String {
        var where = ""
        builder.where?.run {
            lines.forEach {
                where += when(it.concatKeyword) {
                    QueryBuilder.Conditional.Concat.START -> {
                        "WHERE ${it.condition}"
                    }

                    QueryBuilder.Conditional.Concat.OR -> {
                        " OR ${it.condition}"
                    }

                    QueryBuilder.Conditional.Concat.AND -> {
                        " AND ${it.condition}"
                    }
                }
            }
        }

        return when(builder.crudType) {
            QueryBuilder.Crud.SELECT ->
            {
                "SELECT ${builder.column.joinToString()} FROM ${builder.from.joinToString()} $where ${
                    if(builder.group.size > 0) {
                        "GROUP BY ${builder.group.joinToString()} "
                    } else ""
                } ${
                    if(builder.orderBy.isNotEmpty()) {
                        "ORDER BY ${builder.orderBy.entries.joinToString {
                            "${it.key} ${it.value.name}"
                        }}"
                    } else ""
                }"
            }
            QueryBuilder.Crud.UPDATE ->
            {
                "UPDATE ${builder.tableName} SET ${builder.values.entries.joinToString()} $where"
            }
            QueryBuilder.Crud.INSERT ->
            {
                "INSERT INTO ${builder.tableName} (${builder.values.keys.joinToString()}) VALUES (${builder.values.values.joinToString()})"
            }
            QueryBuilder.Crud.DELETE ->
            {
                "DELETE FROM ${builder.tableName} $where"
            }
        }
    }

    fun newResult(builder: QueryBuilder): ResultSet? {
        return if(dbService.isConnected()) {
            dbService.executeQuery(
                builderToQuery(builder)
            )
        } else null
    }

    fun execute(builder: QueryBuilder): Nothing? {
        return try {
            if(dbService.isConnected())
                dbService.execute(
                    builderToQuery(builder)
                )
            null
        } catch(e: SQLiteException) {
            throw e
        }
    }

}