package com.happysttim.weacord.core.database.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Table(
    val tableName: String
)
