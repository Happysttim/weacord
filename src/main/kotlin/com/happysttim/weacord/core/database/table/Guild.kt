package com.happysttim.weacord.core.database.table

import com.happysttim.weacord.core.database.annotation.*

@Table("Guild")
data class Guild(
    @Column(columnName = "guildID", columnType = ColumnType.INTEGER)
    @NotNull
    @PrimaryKey
    var gid: Long = 0,

    @Column(columnName = "channelName", columnType = ColumnType.STRING)
    @NotNull
    var channelName: String = "",

    @Column(columnName = "channelID", columnType = ColumnType.INTEGER)
    @NotNull
    var channelID: Long = 0,

    @Column(columnName = "isLive", columnType = ColumnType.INTEGER)
    @NotNull
    @Check("isLive IN (0, 1)")
    var isLive: Int = 1,

    @Column(columnName = "checkAlarm", columnType = ColumnType.INTEGER)
    @Check("checkAlarm IN (0, 1)")
    @NotNull
    var isAlarm: Int = 1
)
