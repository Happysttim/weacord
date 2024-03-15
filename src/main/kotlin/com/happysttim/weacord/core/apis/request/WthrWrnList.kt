package com.happysttim.weacord.core.apis.request

import com.happysttim.weacord.core.data.ContentTitle
import kotlin.reflect.KClass

class WthrWrnList: IApisRequest() {
    override fun path(): String = "getWthrWrnList"
    override fun responseType(): KClass<*> = ContentTitle::class

    val numOfRows: Int = 25
    val dataType: String = "JSON"
    val stnId: Int = 108
    var pageNo: Int = 1
}