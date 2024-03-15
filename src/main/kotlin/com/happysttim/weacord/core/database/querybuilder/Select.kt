package com.happysttim.weacord.core.database.querybuilder

import com.happysttim.weacord.core.database.Schema

class Select : QueryBuilder.Builder() {

    fun from(vararg tables: String): Select {
        fromList.addAll(tables)
        return this
    }

    fun column(vararg c: String): Select {
        columnList.addAll(c)
        return this
    }

    fun columnAll(vararg tables: String): Select {
        val schemas = Schema.schemas()

        tables.forEach { tableName ->
            val schema = schemas[tableName]
            schema?.run {
                columnFields.keys.forEach {
                    columnList.add("${tableName}.${it}")
                }
            }
        }

        return this
    }

    fun where(receiver: QueryBuilder.Conditional.() -> QueryBuilder.Conditional): Select {
        whereList = QueryBuilder.Conditional()
        whereList?.receiver()

        return this
    }

    fun orderBy(vararg pair: Pair<String, QueryBuilder.OrderBy>): Select {
        orderByMap.putAll(pair)

        return this
    }

    fun groupBy(vararg groups: String): Select {
        groupList.addAll(groups)

        return this
    }

    override fun build(): QueryBuilder {
        crudType = QueryBuilder.Crud.SELECT
        return super.build()
    }

}