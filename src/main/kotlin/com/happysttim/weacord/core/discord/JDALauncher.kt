package com.happysttim.weacord.core.discord

import com.happysttim.weacord.core.database.Schema
import com.happysttim.weacord.core.database.table.Guild
import com.happysttim.weacord.core.discord.listener.EventListener
import com.happysttim.weacord.core.discord.listener.CommandListener
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent

class JDALauncher private constructor() {

    companion object {
        private var instance: JDALauncher? = null

        fun getInstance(): JDALauncher {
            return instance ?: synchronized(this) {
                JDALauncher().also {
                    instance = it
                }
            }
        }
    }

    private lateinit var jda: JDA

    fun start() {
        jda = JDABuilder.createDefault(
            Dotenv.load().get("DISCORD_TOKEN"),
            GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT
        ).addEventListeners(CommandListener(), EventListener()).build()

        jda.awaitReady()
    }

    fun shutdown() {
        jda.shutdown()
    }

    fun sendBroadcastMessage(message: String) {
        Schema.Search<Guild>("Guild").call().forEach { guild ->
            if(guild.isAlarm == 1) {
                jda.guilds.find {
                    it.idLong == guild.gid
                }?.let {
                    it.textChannels.find { channel ->
                        channel.idLong == guild.channelID
                    }?.sendMessage(message)?.queue()
                }
            }
        }
    }

    fun sendMessage(guild: Guild, message: String) {
        if(guild.isAlarm == 1) {
            jda.guilds.find {
                it.idLong == guild.gid
            }?.let {
                it.textChannels.find { channel ->
                    channel.idLong == guild.channelID
                }?.sendMessage(message)?.queue()
            }
        }
    }
}