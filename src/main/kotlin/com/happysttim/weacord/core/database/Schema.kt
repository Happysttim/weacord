package com.happysttim.weacord.core.database

import com.happysttim.weacord.core.database.annotation.*
import com.happysttim.weacord.core.database.querybuilder.QueryBuilder
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException
import kotlin.reflect.*
import kotlin.reflect.full.*

object Schema {

    enum class ConstraintType {
        NOT_NULL,
        CHECK,
        PRIMARY_KEY,
        FOREIGN_KEY
    }

    data class ColumnFieldData(
        val property: KProperty1<out Any, *>,
        val column: Column,
        val defaultValue: Any? = null,
        val constraints: MutableMap<ConstraintType, Any?> = mutableMapOf()
    ) {
        fun addConstraint(constraint: ConstraintType) {
            constraints[constraint] = null
        }

        fun addCheck(check: String) {
            constraints[ConstraintType.CHECK] = check
        }

        fun addForeign(foreign: ForeignKey) {
            constraints[ConstraintType.FOREIGN_KEY] = foreign
        }
    }

    class TableSchema(val tableClass: KClass<*>) {

        val columnFields: MutableMap<String, ColumnFieldData> = mutableMapOf()

        fun addColumn(columnName: String, columnFieldData: ColumnFieldData) {
            columnFields[columnName] = columnFieldData
        }

    }

    private val schemas: MutableMap<String, TableSchema> = mutableMapOf()
    private val dbService: DatabaseService = DatabaseService.getInstance()

    class Search<T>(private val tableName: String) {

        private val orderMap: MutableMap<String, QueryBuilder.OrderBy> = mutableMapOf()
        private var conditional: QueryBuilder.Conditional? = null
        private var limit: Int = 0

        fun where(receive: QueryBuilder.Conditional.() -> QueryBuilder.Conditional): Search<T> {
            conditional = QueryBuilder.Conditional()
            conditional?.receive()

            return this
        }

        fun orderBy(columnName: String, orderBy: QueryBuilder.OrderBy = QueryBuilder.OrderBy.ASC): Search<T> {
            orderMap[columnName] = orderBy

            return this
        }

        fun limit(l: Int): Search<T> {
            limit = l

            return this
        }

        fun call(): List<T> {
            return synchronized(this) {
                val domain: MutableList<T> = mutableListOf()

                schemas[tableName]?.run {
                    val result = dbService.executeQuery(
                        "SELECT * FROM $tableName ${conditionToString()} ${orderToString()} ${limitToString()}"
                    )

                    val cons = tableClass.primaryConstructor

                    while(result != null && result.next()) {
                        val propertyMatch = columnFields.keys.toList().associate { columnFields[it]?.property?.name to result.getObject(it) }
                        @Suppress("UNCHECKED_CAST")
                        domain.add(
                            cons?.parameters?.associate {
                                it to propertyMatch[it.name]
                            }?.let {
                                cons.callBy(it)
                            }!! as T
                        )
                    }
                }

                domain
            }
        }

        private fun orderToString(): String {
            return if(orderMap.isEmpty()) ""
            else {
                "ORDER BY ${
                    orderMap.entries.joinToString {
                        "${it.key} ${it.value.name}"
                    }
                }"
            }
        }

        private fun conditionToString(): String {
            var where = ""
            conditional?.run {
                lines.forEach {
                    where += when (it.concatKeyword) {
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

            return where
        }

        private fun limitToString(): String {
            return if(limit == 0) "" else "LIMIT $limit"
        }

    }

    fun register(vararg tables: KClass<*>): Nothing? {
        return try {
            tables.forEach {tableClass ->
                if(!tableClass.isData) {
                    throw SQLiteException("테이블이 data class 이 아닙니다.", SQLiteErrorCode.SQLITE_ERROR)
                }

                val schema = TableSchema(tableClass)
                val tableName: String = (tableClass.findAnnotation<Table>()?.tableName ?: tableClass.simpleName)!!

                val cons = tableClass.primaryConstructor
                val instance = cons?.parameters?.filterNot {
                    it.isOptional
                }?.associate {
                    it to primitiveDefaultValue(it)
                }?.let {
                    cons.callBy(
                        it
                    )
                }

                tableClass.declaredMemberProperties.forEach { member ->
                    val column = member.findAnnotation<Column>()
                    val notNull = member.findAnnotation<NotNull>()
                    val primaryKey = member.findAnnotation<PrimaryKey>()
                    val check = member.findAnnotation<Check>()
                    val foreignKey = member.findAnnotation<ForeignKey>()

                    if(column == null) {
                        throw SQLiteException("Column 데이터를 입력해야 합니다.", SQLiteErrorCode.SQLITE_ERROR)
                    }

                    val columnFieldData = ColumnFieldData(
                        property = member,
                        column = column,
                        defaultValue = member.getter.call(instance)
                    )

                    if(notNull != null) {
                        columnFieldData.addConstraint(ConstraintType.NOT_NULL)
                    }

                    if(primaryKey != null) {
                        columnFieldData.addConstraint(ConstraintType.PRIMARY_KEY)
                    }

                    if(check != null) {
                        columnFieldData.addCheck(check.whereSyntax)
                    }

                    if(foreignKey != null) {
                        columnFieldData.addForeign(foreignKey)
                    }

                    schema.addColumn(column.columnName, columnFieldData)
                }

                schemas[tableName] = schema
            }
            null
        } catch(e: SQLiteException) {
            throw e
        }
    }

    fun up() {
        schemas.forEach { (tableName, schema) ->
            var columnStr = ""
            var constraintStr = ""
            for(columnField in schema.columnFields) {
                columnStr += "${ columnField.key } ${
                    when(columnField.value.column.columnType) {
                        ColumnType.STRING -> "STRING"
                        ColumnType.INTEGER -> "INTEGER"
                        ColumnType.TEXT -> "TEXT"
                        ColumnType.BLOB -> "BLOB"
                        ColumnType.REAL -> "REAL"
                        ColumnType.NUMERIC -> "NUMERIC"
                    }
                } "

                columnField.value.constraints.forEach { (constraint, value) ->
                    if(constraint != ConstraintType.FOREIGN_KEY && constraint != ConstraintType.PRIMARY_KEY) {
                        columnStr += " ${ constraint.name }"
                        if(constraint == ConstraintType.CHECK) {
                            columnStr += " (${ value.toString() })"
                        }
                        if(columnField.value.column.autoIncrement) {
                            columnStr += " AUTOINCREMENT"
                        }
                    } else {
                        constraintStr += """
                            ${ 
                                when(constraint) {
                                    ConstraintType.PRIMARY_KEY -> "PRIMARY KEY(${columnField.value.column.columnName}), "
                                    ConstraintType.FOREIGN_KEY -> { 
                                        val fk = value as ForeignKey
                                        "FOREIGN KEY(${columnField.value.column.columnName}) REFERENCES ${fk.tableName}(${fk.columnName}), "
                                    }
                                    else -> ""
                                }
                            }
                        """.trimIndent()
                    }
                }

                columnStr += ", "
            }
            val sqlQuery = """
                CREATE TABLE IF NOT EXISTS $tableName (
                    ${
                        if(constraintStr.trimIndent() == "") {
                            columnStr.substring(0, columnStr.length - 2)
                        } else {
                            columnStr + constraintStr.substring(0, constraintStr.length - 2)
                        }
                    }
                )
            """.trimIndent()

            dbService.execute(sqlQuery)
        }
    }

    fun down() {
        schemas.forEach { (tableName, _) ->
            dbService.execute("DROP TABLE IF EXISTS $tableName")
        }
    }

    fun <T> find(tableName: String, pk: Any): T? {
        val value = if(pk is String) {
            "\"$pk\""
        } else pk

        schemas[tableName]?.run {
            for(columnField in columnFields) {
                if(columnField.value.constraints.keys.find {
                        it == ConstraintType.PRIMARY_KEY
                    } != null) {
                    val result = dbService.executeQuery(
                        "SELECT * FROM $tableName WHERE ${columnField.key} = $value"
                    )

                    return if(result == null) {
                        null
                    } else if(result.next()) {
                        val cons = tableClass.primaryConstructor
                        val propertyMatch = columnFields.keys.toList().associate { columnFields[it]?.property?.name to result.getObject(it) }
                        @Suppress("UNCHECKED_CAST")
                        cons?.parameters?.associate {
                            it to propertyMatch[it.name]
                        }?.let {
                            cons.callBy(it)
                        }!! as T
                    } else {
                        null
                    }
                }
            }
        }

        return null
    }

    fun <T> insert(data: T): Nothing? {
        return try {
            val clazz = data!!::class
            val tableName = clazz.findAnnotation<Table>()?.tableName ?: clazz.simpleName
            var sqlQuery = "INSERT INTO $tableName ("

            schemas[tableName]?.run {
                sqlQuery += columnFields.keys.joinToString() + ") VALUES ("
                columnFields.forEach {columnField ->
                    val value = columnField.value.property.getter.call(data)
                    sqlQuery += if (value is String) "\"$value\", "
                    else "$value, "
                }

                sqlQuery = "${sqlQuery.substring(0, sqlQuery.length - 2)})"
                dbService.execute(sqlQuery)
            }

            null
        } catch(e: SQLiteException) {
            throw e
        }
    }

    fun <T> update(data: T): Nothing? {
        return try {
            val clazz = data!!::class
            val tableName = clazz.findAnnotation<Table>()?.tableName ?: clazz.simpleName

            schemas[tableName]?.run {
                val whereList: MutableMap<String, String> = mutableMapOf()
                val updateList: MutableMap<String, String> = mutableMapOf()

                columnFields.forEach { (columnName, columnField) ->
                    val value = columnField.property.getter.call(data)
                    if(columnField.constraints.keys.find { it == ConstraintType.PRIMARY_KEY } != null) {
                        whereList[columnName] = if(value is String) "\"$value\""
                                                else "$value"
                    } else {
                        updateList[columnName] = if(value is String) "\"$value\""
                                                 else "$value"
                    }
                }

                val sqlQuery = "UPDATE $tableName SET ${ updateList.entries.joinToString() } ${
                    if(whereList.isNotEmpty()) {
                        " WHERE ${whereList.entries.joinToString(separator = " AND ")}"   
                    } else ""
                }"
                dbService.execute(sqlQuery)
            }

            null
        } catch(e: SQLiteException) {
            throw e
        }
    }

    fun <T> delete(data: T): Nothing? {
        return try {
            val clazz = data!!::class
            val tableName = clazz.findAnnotation<Table>()?.tableName ?: clazz.simpleName

            schemas[tableName]?.run {
                val whereList: MutableMap<String, String> = mutableMapOf()

                columnFields.forEach { (columnName, columnField) ->
                    val value = columnField.property.getter.call(data)
                    whereList[columnName] = if(value is String) "\"$value\""
                                            else "$value"

                }

                val sqlQuery = "DELETE FROM $tableName ${
                    if(whereList.isNotEmpty()) {
                        " WHERE ${whereList.entries.joinToString(separator = " AND ")}"
                    } else ""
                }"

                dbService.execute(sqlQuery)
            }

            null
        } catch(e: SQLiteException) {
            throw e
        }
    }

    fun delete(tableName: String): Nothing? {
        return try {
            schemas[tableName]?.run {
                dbService.execute("DELETE FROM $tableName")
            }

            null
        } catch(e: SQLiteException) {
            throw e
        }
    }

    fun schemas(): MutableMap<String, TableSchema> = schemas

    private fun primitiveDefaultValue(param: KParameter): Any? {
        return when(param.type) {
            typeOf<Byte>(), typeOf<Short>(), typeOf<Int>(), typeOf<Long>() -> 0
            typeOf<Float>(), typeOf<Double>() -> 0.0
            typeOf<String>() -> ""
            typeOf<Char>() -> ' '
            else -> null
        }
    }

}