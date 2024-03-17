package com.happysttim.weacord

import com.happysttim.weacord.core.apis.ApisHttp
import com.happysttim.weacord.core.apis.listener.BreakNewsListener
import com.happysttim.weacord.core.apis.listener.PwnCdListener
import com.happysttim.weacord.core.apis.request.PwnCd
import com.happysttim.weacord.core.apis.request.WthrBrkNews
import com.happysttim.weacord.core.database.DatabaseService
import com.happysttim.weacord.core.database.QueryExecutor
import com.happysttim.weacord.core.database.Schema
import com.happysttim.weacord.core.database.querybuilder.Delete
import com.happysttim.weacord.core.database.table.BreakNewsCode
import com.happysttim.weacord.core.database.table.Guild
import com.happysttim.weacord.core.database.table.NationalBreakNews
import com.happysttim.weacord.core.database.table.WeatherNews
import com.happysttim.weacord.core.discord.JDALauncher
import com.happysttim.weacord.utils.Logger
import com.happysttim.weacord.utils.SharedDate
import org.sqlite.SQLiteException
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class Application {

    private val logging = Logger.getLogger<Application>()

    fun start() {
        val dbService = DatabaseService.getInstance()
        val launcher = JDALauncher.getInstance()
        val http = ApisHttp(1L, TimeUnit.MINUTES)

        logging.info("서비스 준비중입니다...")
        dbService.start()

        Schema.register(
            BreakNewsCode::class,
            Guild::class,
            NationalBreakNews::class,
            WeatherNews::class
        )

        Schema.up()

        http.registerListener(PwnCd(), PwnCdListener())
        http.registerListener(WthrBrkNews(), BreakNewsListener())

        launcher.start()
        http.start()

        logging.info("디스코드 봇이 시작되었습니다!")

        timer(
            initialDelay = 0,
            period = 600 * 1000L
        ) {
            val queryExecutor = QueryExecutor()
            val now = LocalDateTime.now()

            if(!SharedDate.compare(now)) {
                SharedDate.update()
            }

            val today = SharedDate.getDateOnTime()

            try {
                queryExecutor.execute(
                    Delete().from("BreakNewsCode").where {
                        first("tmFc < $today")
                    }.build()
                )
                queryExecutor.execute(
                    Delete().from("NationalBreakNews").where {
                        first("tmFc < $today")
                    }.build()
                )
                queryExecutor.execute(
                    Delete().from("WeatherNews").where {
                        first("tmFc < $today")
                    }.build()
                )
            } catch(e: SQLiteException) {
                logging.error(e.message)
            }
        }
    }
}

fun main() {
    Application().start()
}