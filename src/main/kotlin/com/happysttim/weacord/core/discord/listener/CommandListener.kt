package com.happysttim.weacord.core.discord.listener

import com.happysttim.weacord.core.database.Schema
import com.happysttim.weacord.core.database.table.Guild
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.sqlite.SQLiteException

private val logging = KotlinLogging.logger {  }

class CommandListener: ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val guild = event.guild

        if(event.name == "날씨특보") {
            try {
                event.getOption("채널")?.asChannel?.let {
                    if(it.type == ChannelType.TEXT) {
                        Schema.find<Guild>("Guild", guild!!.idLong)?.run {
                            channelID = it.idLong
                            channelName = it.name
                            Schema.update(this)
                        }
                        event.reply("새로운 알림이 이제 ${it.name} 채널에서 알려집니다!").queue()
                    } else event.reply("날씨 알림은 텍스트 채널에서만 받을 수 있습니다!").queue()
                }

                event.getOption("알림")?.asBoolean?.let {
                    Schema.find<Guild>("Guild", guild!!.idLong)?.run {
                        isAlarm = if(it) 1 else 0
                        Schema.update(this)
                    }
                    event.reply("날씨 알림을 ${ if(it) "ON" else "OFF" } 했습니다!").queue()
                }
            } catch(e: SQLiteException) {
                logging.error { e.message }
            }
        }
    }
}