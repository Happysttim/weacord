package com.happysttim.weacord.core.apis.request

import com.happysttim.weacord.core.data.News
import kotlin.reflect.KClass

class WthrBrkNews: IApisRequest() {
    override fun path(): String = "getWthrBrkNews"
    override fun responseType(): KClass<*> = News::class

    val dataType: String = "JSON"
    val numOfRows: Int = 25
    val stnId: Int = 108
    var pageNo: Int = 1
}