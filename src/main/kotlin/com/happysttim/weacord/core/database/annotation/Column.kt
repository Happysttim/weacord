package com.happysttim.weacord.core.database.annotation

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(
    val columnName: String,
    val columnType: ColumnType,
    val autoIncrement: Boolean = false
)
