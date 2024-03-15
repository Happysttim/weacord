package com.happysttim.weacord.core.database.querybuilder

class QueryBuilder {

    lateinit var crudType: Crud
    lateinit var column: MutableList<String>
    lateinit var from: MutableList<String>
    lateinit var group: MutableList<String>
    lateinit var orderBy: MutableMap<String, OrderBy>
    lateinit var tableName: String
    lateinit var values: MutableMap<String, Any>
    var where: Conditional? = null

    enum class Crud {
        SELECT,
        INSERT,
        UPDATE,
        DELETE
    }

    enum class OrderBy {
        ASC,
        DESC
    }

    class Conditional {

        enum class Concat {
            AND,
            OR,
            START
        }

        data class Line(
            val concatKeyword: Concat,
            val condition: String
        )

        val lines: MutableList<Line> = mutableListOf(Line(Concat.START, ""))

        fun first(line: String, vararg target: Any): Conditional {
            lines[0] = Line(Concat.START, format(line, *target))
            return this
        }

        fun add(line: String, vararg target: Any): Conditional {
            lines.add(
                Line(Concat.AND, format(line, *target))
            )

            return this
        }

        fun or(line: String, vararg target: Any): Conditional {
            lines.add(
                Line(Concat.OR, format(line, *target))
            )

            return this
        }

        private fun format(line: String, vararg format: Any): String {
            val regex = """\{\d\}""".toRegex()
            val matchResult = regex.findAll(line).iterator()
            val formatIterator = format.iterator()

            var result = line

            while(matchResult.hasNext() && formatIterator.hasNext()) {
                var target = formatIterator.next()
                if(target is String) {
                    target = "\"${target.replace("\"", "")}\""
                }
                result = result.replace(matchResult.next().value, target.toString())
            }

            return result
        }

    }

    constructor(
        crudType: Crud,
        column: MutableList<String>,
        from: MutableList<String>,
        where: Conditional?,
        group: MutableList<String>,
        orderBy: MutableMap<String, OrderBy>
    ) {
        this.crudType = crudType
        this.column = column
        this.from = from
        this.where = where
        this.group = group
        this.orderBy = orderBy
    }

    constructor(
        crudType: Crud,
        set: MutableMap<String, Any>,
        tableName: String,
        where: Conditional?,
    ) {
        this.crudType = crudType
        this.values = set
        this.tableName = tableName
        this.where = where
    }

    constructor(
        crudType: Crud,
        tableName: String,
        where: Conditional?,
    ) {
        this.crudType = crudType
        this.tableName = tableName
        this.where = where
    }

    constructor(
        crudType: Crud,
        values: MutableMap<String, Any>,
        tableName: String,
    ) {
        this.crudType = crudType
        this.values = values
        this.tableName = tableName
    }

    open class Builder {
        protected constructor()

        protected open val columnList: MutableList<String> = mutableListOf()
        protected open val fromList: MutableList<String> = mutableListOf()
        protected open var whereList: Conditional? = null
        protected open val groupList: MutableList<String> = mutableListOf()
        protected open val orderByMap: MutableMap<String, OrderBy> = mutableMapOf()
        protected open val valueMap: MutableMap<String, Any> = mutableMapOf()
        protected open var tableName: String = ""
        protected open lateinit var crudType: Crud

        protected fun checkString(target: Any): Any {
            if(target !is String) return target
            return "\"${target}\""
        }

        open fun build(): QueryBuilder {
            return when(crudType) {
                Crud.SELECT -> QueryBuilder(
                    crudType,
                    columnList,
                    fromList,
                    whereList,
                    groupList,
                    orderByMap
                )
                Crud.INSERT -> QueryBuilder(
                    crudType,
                    valueMap,
                    tableName
                )
                Crud.UPDATE -> QueryBuilder(
                    crudType,
                    valueMap,
                    tableName,
                    whereList
                )
                Crud.DELETE -> QueryBuilder(
                    crudType,
                    tableName,
                    whereList
                )
            }
        }
    }
}