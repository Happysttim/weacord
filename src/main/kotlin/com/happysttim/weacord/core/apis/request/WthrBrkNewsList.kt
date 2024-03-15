package com.happysttim.weacord.core.apis.request

import com.happysttim.weacord.core.data.ContentTitle
import kotlin.reflect.KClass

class WthrBrkNewsList: IApisRequest() {
    override fun path(): String = "getWthrBrkNewsList"
    override fun responseType(): KClass<*> = ContentTitle::class

    val dataType: String = "JSON"
    val numOfRows: Int = 25
    val stnId: Int = 108
    var pageNo: Int = 1
}