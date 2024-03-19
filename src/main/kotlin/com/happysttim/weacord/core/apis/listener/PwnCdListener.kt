package com.happysttim.weacord.core.apis.listener

import com.happysttim.weacord.utils.TmFc
import com.happysttim.weacord.core.data.Cd
import com.happysttim.weacord.core.database.Schema
import com.happysttim.weacord.core.database.querybuilder.QueryBuilder
import com.happysttim.weacord.core.database.table.BreakNewsCode
import com.happysttim.weacord.core.database.table.Guild
import com.happysttim.weacord.core.discord.JDALauncher
import com.happysttim.weacord.utils.Logger
import com.happysttim.weacord.utils.Weather

class PwnCdListener: IApisListener<Cd> {

    private val logging = Logger.getLogger<PwnCdListener>()

    override fun onTask(cd: Cd?) {
        try {
            val today = TmFc.getDateOnTime()
            val latest = Schema.Search<BreakNewsCode>("BreakNewsCode").orderBy("tmFc", QueryBuilder.OrderBy.DESC).where {
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

                    if(latest.find { it == breakNewsCode } == null) {
                        logging.info("신규 저장 $breakNewsCode")
                        Schema.insert(breakNewsCode)
                        newData++
                    } else break
                }

                if(newData > 0) {
                    val updated = Schema.Search<BreakNewsCode>("BreakNewsCode").limit(newData).orderBy("tmFc", QueryBuilder.OrderBy.DESC).call().reversed()
                    val guilds = Schema.Search<Guild>("Guild").call()

                    updated.forEach { update ->
                        val tmFc = update.tmFc.toString()
                        val startTime = update.startTime.toString()
                        val endTime = update.endTime.toString()
                        guilds.forEach { guild ->
                            JDALauncher.getInstance().sendMessage(guild, "```${ tmFc.substring(0, 4) }년 ${ tmFc.substring(4, 6) }월 ${ tmFc.substring(6, 8) }일 ${ tmFc.substring(8, 10) }:${ tmFc.substring(10, 12) } ${ update.areaName } ${ Weather.typeToName(update.warnVar) }${ Weather.stressToName(update.warnStress) } ${ Weather.commandToName(update.command) }\n" +
                                    "${
                                        if(startTime != "0") {
                                            "발표시각: ${ startTime.substring(0, 4) }년 ${ startTime.substring(4, 6) }월 ${ startTime.substring(6, 8) }일 ${ startTime.substring(8, 10) }:${ startTime.substring(10, 12) }"
                                        } else if(endTime != "0") {
                                            "해제시각: ${ endTime.substring(0, 4) }년 ${ endTime.substring(5, 6) }월 ${ endTime.substring(6, 8) }일 ${ endTime.substring(8, 10) }:${ endTime.substring(10, 12) }"
                                        } else ""
                                    }```")
                        }
                    }
                }
            }

            logging.info("총 $newData 개의 신규 데이터를 저장했습니다.")
        } catch(e: Exception) {
            logging.error(e.message)
        }
    }
}