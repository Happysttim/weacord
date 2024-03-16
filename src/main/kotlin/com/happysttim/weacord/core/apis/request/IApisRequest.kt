package com.happysttim.weacord.core.apis.request

import com.happysttim.weacord.utils.SharedDate
import io.github.cdimascio.dotenv.Dotenv
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

abstract class IApisRequest {
    val serviceKey: String = Dotenv.load().get("OPEN_API_TOKEN")
    var fromTmFc: String = SharedDate.getDate().toString()
    var toTmFc: String = SharedDate.getDate().toString()
    abstract fun path(): String
    abstract fun responseType(): KClass<*>

    fun setTmFc(tmFc: String) {
        fromTmFc = tmFc
        toTmFc = tmFc
    }

    fun query(): String {
        val instance = this::class
        val mutableMap: MutableMap<String, Any?> = mutableMapOf()

        instance.memberProperties.forEach {
            mutableMap[it.name] = it.getter.call(this)
        }

        return mutableMap.entries.joinToString(
            prefix = "?",
            separator = "&",
        )
    }
}