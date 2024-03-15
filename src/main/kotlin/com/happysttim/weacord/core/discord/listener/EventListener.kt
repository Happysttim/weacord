package com.happysttim.weacord.core.discord.listener

import com.happysttim.weacord.core.database.Schema
import com.happysttim.weacord.core.database.table.Guild
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.sqlite.SQLiteException

private val logging = KotlinLogging.logger {  }

class EventListener : ListenerAdapter() {

    override fun onGuildReady(event: GuildReadyEvent) {
        val guild = event.guild
        val data = Schema.find<Guild>("Guild", guild.id)

        guild.updateCommands().addCommands(
            Commands.slash("날씨특보", "특보알리미 봇에 대한 설정을 다루는 명령어 입니다")
                .addOption(OptionType.CHANNEL, "채널", "알림이 어떤 채널에 올 수 있는지 설정합니다.")
                .addOption(OptionType.BOOLEAN, "알림", "특보 알림을 끄거나 킬 수 있습니다.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_PERMISSIONS)),
        ).queue()

        try {
            if(data == null) {
                Schema.insert(
                    Guild(
                        gid = guild.idLong,
                        channelID = guild.textChannels[0].idLong,
                        channelName = guild.textChannels[0].name,
                        isLive = 1,
                        isAlarm = 1
                    )
                )
                logging.info { "${guild.name}(${guild.idLong}) 서버가 감지되었습니다." }
            } else {
                data.isLive = 1
                Schema.update(data) == null
                logging.info { "${guild.name}(${guild.idLong}) 서버가 감지되었습니다." }
            }
        } catch(e: SQLiteException) {
            logging.error { e.message }
        }
    }

    override fun onReady(event: ReadyEvent) {
        try {
            val targets = Schema.Search<Guild>("Guild").where {
                first("isLive = 0")
            }.call()
            targets.forEach {
                Schema.delete(it)
            }

            logging.info { "감지되지 않은 ${ targets.size }개의 서버를 삭제했습니다." }
        } catch(e: SQLiteException) {
            logging.error { e.message }
        }
    }



    override fun onGuildJoin(event: GuildJoinEvent) {
        val guild = event.guild
        guild.updateCommands().addCommands(
            Commands.slash("날씨특보", "특보알리미 봇에 대한 설정을 다루는 명령어 입니다")
                .addOption(OptionType.CHANNEL, "채널", "알림이 어떤 채널에 올 수 있는지 설정합니다.")
                .addOption(OptionType.BOOLEAN, "알림", "특보 알림을 끄거나 킬 수 있습니다.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_PERMISSIONS)),
        ).queue()

        try {
            Schema.insert(
                Guild(
                    gid = guild.idLong,
                    channelID = guild.textChannels[0].idLong,
                    channelName = guild.textChannels[0].name
                )
            )

            logging.info { "${guild.name}(${guild.idLong}) 서버가 감지되었습니다." }
        } catch(e: SQLiteException) {
            logging.error { e.message }
        }

    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        val guild = event.guild

        try {
            Schema.find<Guild>("Guild", guild.id)?.run {
                Schema.delete(this)
                logging.info { "${guild.name}(${guild.idLong}) 서버에서 추방당했습니다." }
            }
        } catch(e: SQLiteException) {
            logging.error { e.message }
        }
    }
}