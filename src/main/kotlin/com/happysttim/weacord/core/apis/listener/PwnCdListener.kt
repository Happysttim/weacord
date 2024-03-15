package com.happysttim.weacord.core.apis.listener

import com.happysttim.weacord.utils.SharedDate
import com.happysttim.weacord.core.data.Cd
import com.happysttim.weacord.core.database.Schema
import com.happysttim.weacord.core.database.querybuilder.QueryBuilder
import com.happysttim.weacord.core.database.table.BreakNewsCode
import com.happysttim.weacord.core.discord.JDALauncher
import com.happysttim.weacord.utils.Weather
import io.github.oshai.kotlinlogging.KotlinLogging

private val logging = KotlinLogging.logger {  }

class PwnCdListener: IApisListener<Cd> {
    override fun onTask(cd: Cd?) {
        val today = SharedDate.getDateOnTime()
        val search = Schema.Search<BreakNewsCode>("BreakNewsCode")
        val latest = search.orderBy("tmFc", QueryBuilder.OrderBy.DESC).where {
            first("tmFc >= $today")
        }.call()
        var newData = 0

        cd?.message?.body?.run {
            for(item in items.itemList) {
                val breakNewsCode = BreakNewsCode(
                    tmSeq = item.tmSeq,
                    allEndTime = item.allEndTime,
                    areaCode = item.areaCode,
                    areaName = item.areaName,
                    cancel = item.cancel,
                    command = item.command,
                    endTime = item.endTime,
                    stnId = item.stnId,
                    tmFc = item.tmFc,
                    warnVar = item.warnVar,
                    warnStress = item.warnStress,
                    startTime = item.startTime
                )

                if(latest.find { bnc -> bnc == breakNewsCode } == null) {
                    Schema.insert(breakNewsCode)
                    newData++
                } else break
            }

            if(newData > 0) {
                val updated = search.limit(newData).orderBy("tmFc", QueryBuilder.OrderBy.DESC).call()

                updated.forEach {
                    val tmFc = it.tmFc.toString()
                    val startTime = it.startTime.toString()
                    val endTime = it.endTime.toString()

                    JDALauncher.getInstance().sendBroadcastMessage("```${ tmFc.substring(0, 4) }년 ${ tmFc.substring(4, 6) }월 ${ tmFc.substring(6, 8) }일 ${ tmFc.substring(8, 10) }:${ tmFc.substring(10, 12) } ${ it.areaName } ${ Weather.typeToName(it.warnVar) }${ Weather.stressToName(it.warnStress) } ${ Weather.commandToName(it.command) }\n" +
                            "${
                                if(startTime != "") {
                                    "발표시각: ${ startTime.substring(0, 4) }년 ${ startTime.substring(4, 6) }월 ${ startTime.substring(6, 8) }일 ${ startTime.substring(8, 10) }:${ startTime.substring(10, 12) }"
                                } else if(endTime != "") {
                                    "해제시각: ${ endTime.substring(0, 4) }년 ${ endTime.substring(5, 6) }월 ${ endTime.substring(6, 8) }일 ${ endTime.substring(8, 10) }:${ endTime.substring(10, 12) }"
                                } else ""
                            }```")
                }
            }
        }

        logging.info { "총 $newData 개의 신규 데이터를 저장했습니다." }
    }
}