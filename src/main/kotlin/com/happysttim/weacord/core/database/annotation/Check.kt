package com.happysttim.weacord.core.database.annotation

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Check(
    val whereSyntax: String
)
