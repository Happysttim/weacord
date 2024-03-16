package com.happysttim.weacord.core.apis

import com.google.gson.Gson
import com.happysttim.weacord.core.apis.listener.IApisListener
import com.happysttim.weacord.core.apis.request.IApisRequest
import com.happysttim.weacord.utils.Logger
import kotlinx.coroutines.Runnable
import okhttp3.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ApisHttp(private val interval: Long, private val timeUnit: TimeUnit) {
    private val logging = Logger.getLogger<ApisHttp>()
    private val basePath: String = "http://apis.data.go.kr/1360000/WthrWrnInfoService/"
    private val httpClient = okhttp3.OkHttpClient.Builder().connectionPool(
        ConnectionPool(8, 5L, TimeUnit.MINUTES)
    ).connectTimeout(30, TimeUnit.SECONDS).build()
    private val scheduleListener: MutableMap<IApisRequest, IApisListener<Any>?> = mutableMapOf()
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val gson: Gson = Gson()
    private val runnable: Runnable = Runnable {
        scheduleListener.onEach {
            val builder = Request.Builder().url(
                "$basePath${it.key.path()}${it.key.query()}"
            ).get().build()

            try {
                val response = httpClient.newCall(builder).execute()

                val responseMessage = gson.fromJson(response.body?.string(), it.key.responseType().java)
                if(it.value != null) {
                    it.value?.onTask(responseMessage)
                }
            } catch(e: Exception) {
                logging.error("HTTP(${it.key.path()}) 요청 중 에러가 발생했습니다. ${e.message}")
            }
        }
    }

    fun <T> executeRequest(iApis: IApisRequest, iListener: (T) -> Unit) {
        val builder = Request.Builder().url(
            "$basePath${iApis.path()}${iApis.query()}"
        ).get().build()

        val response = httpClient.newCall(builder).execute()

        @Suppress("UNCHECKED_CAST")
        val responseMessage = gson.fromJson(response.body?.string(), iApis.responseType().java) as T
        iListener(responseMessage)
    }

    fun registerListener(iApis: IApisRequest, iListener: IApisListener<Any>?) {
        scheduleListener[iApis] = iListener
    }

    fun start() {
        executor.scheduleWithFixedDelay(runnable, 0, interval, timeUnit)
    }

    fun shutdown() {
        executor.shutdown()
    }
}