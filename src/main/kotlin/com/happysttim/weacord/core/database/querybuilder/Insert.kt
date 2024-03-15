package com.happysttim.weacord.core.database.querybuilder

class Insert: QueryBuilder.Builder() {
    fun from(table: String): Insert {
        tableName = table

        return this
    }

    fun values(vararg valuePair: Pair<String, Any>): Insert {
        valuePair.forEach {
            valueMap[it.first] = checkString(it.second)
        }

        return this
    }

    override fun build(): QueryBuilder {
        crudType = QueryBuilder.Crud.INSERT
        return super.build()
    }
}