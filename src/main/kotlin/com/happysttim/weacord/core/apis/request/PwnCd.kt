package com.happysttim.weacord.core.apis.request

import com.happysttim.weacord.core.data.Cd
import kotlin.reflect.KClass

class PwnCd : IApisRequest() {
    override fun path(): String = "getPwnCd"
    override fun responseType(): KClass<*> = Cd::class

    var pageNo: Int = 1
    var numOfRows: Int = 200
    var dataType: String = "JSON"
}