package com.happysttim.weacord.core.apis.listener

import com.happysttim.weacord.utils.SharedDate
import com.happysttim.weacord.core.data.News
import com.happysttim.weacord.core.database.Schema
import com.happysttim.weacord.core.database.querybuilder.QueryBuilder
import com.happysttim.weacord.core.database.table.WeatherNews
import com.happysttim.weacord.core.discord.JDALauncher
import org.slf4j.LoggerFactory

class BreakNewsListener: IApisListener<News> {

    private val logging = LoggerFactory.getLogger(BreakNewsListener::class.java)
    override fun onTask(news: News?) {
        val today = SharedDate.getDateOnTime()
        val search = Schema.Search<WeatherNews>("WeatherNews")
        val latest = search.where {
            first("tmFc >= $today")
        }.orderBy("tmFc", QueryBuilder.OrderBy.DESC).call().first()

        var newData = 0

        news?.message?.body?.run {
            for(item in items.itemList) {
                val weatherNews = WeatherNews(
                    stnId = item.stnId,
                    tmFc = item.tmFc,
                    ann = item.ann
                )

                if(latest != weatherNews) {
                    Schema.insert(weatherNews)
                    newData++
                } else break
            }

            if(newData > 0) {
                val updated = search.limit(newData).orderBy("tmFc", QueryBuilder.OrderBy.ASC).call()

                updated.forEach {
                    val tmFc = it.tmFc.toString()

                    JDALauncher.getInstance().sendBroadcastMessage("""```${ tmFc.substring(0, 4) }년 ${ tmFc.substring(4, 6) }월 ${ tmFc.substring(6, 8) }일 ${ tmFc.substring(8, 10) }:${ tmFc.substring(10, 12) } 뉴스
                        
                        ${ it.ann.replace("\\n", "\n") }```""".trimIndent())
                }
            }
        }

        logging.info("총 $newData 개의 신규 데이터를 저장했습니다.")
    }
}