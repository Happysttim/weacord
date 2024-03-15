package com.happysttim.weacord.core.database.querybuilder

class Delete: QueryBuilder.Builder() {

    fun from(table: String): Delete {
        tableName = table

        return this
    }

    fun where(receiver: QueryBuilder.Conditional.() -> QueryBuilder.Conditional): Delete {
        whereList = QueryBuilder.Conditional()
        whereList?.receiver()

        return this
    }

    override fun build(): QueryBuilder {
        crudType = QueryBuilder.Crud.DELETE
        return super.build()
    }
}