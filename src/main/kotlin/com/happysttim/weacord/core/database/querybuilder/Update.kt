package com.happysttim.weacord.core.database.querybuilder

class Update: QueryBuilder.Builder() {
    fun from(table: String): Update {
        tableName = table
        return this
    }

    fun set(vararg setPair: Pair<String, Any>): Update {
        setPair.forEach {
            valueMap[it.first] = checkString(it.second)
        }

        return this
    }

    fun where(receiver: QueryBuilder.Conditional.() -> QueryBuilder.Conditional): Update {
        whereList = QueryBuilder.Conditional()
        whereList?.receiver()

        return this
    }

    override fun build(): QueryBuilder {
        crudType = QueryBuilder.Crud.UPDATE
        return super.build()
    }
}