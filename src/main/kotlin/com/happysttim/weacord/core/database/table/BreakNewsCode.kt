package com.happysttim.weacord.core.database.table

import com.happysttim.weacord.core.database.annotation.*

@Table("BreakNewsCode")
data class BreakNewsCode (
    @Column(columnName = "tmSeq", columnType = ColumnType.INTEGER)
    @NotNull
    var tmSeq: Int = 0,

    @Column(columnName = "allEndTime", columnType = ColumnType.STRING)
    @NotNull
    var allEndTime: Long = 0,

    @Column(columnName = "areaCode", columnType = ColumnType.STRING)
    @NotNull
    var areaCode: String = "",

    @Column(columnName = "areaName", columnType = ColumnType.STRING)
    @NotNull
    var areaName: String = "",

    @Column(columnName = "cancel", columnType = ColumnType.INTEGER)
    @NotNull
    var cancel: Int = 0,

    @Column(columnName = "command", columnType = ColumnType.INTEGER)
    @NotNull
    var command: Int = 0,

    @Column(columnName = "endTime", columnType = ColumnType.STRING)
    @NotNull
    var endTime: Long = 0,

    @Column(columnName = "stnId", columnType = ColumnType.INTEGER)
    @NotNull
    var stnId: Int = 0,

    @Column(columnName = "tmFc", columnType = ColumnType.STRING)
    @NotNull
    var tmFc: Long = 0,

    @Column(columnName = "warnVar", columnType = ColumnType.INTEGER)
    @NotNull
    var warnVar: Int = 0,

    @Column(columnName = "warnStress", columnType = ColumnType.INTEGER)
    @NotNull
    var warnStress: Int = 0,

    @Column(columnName = "startTime", columnType = ColumnType.STRING)
    @NotNull
    var startTime: Long = 0,
) {
    @Column(columnName = "id", columnType = ColumnType.INTEGER, autoIncrement = true)
    @PrimaryKey
    var id: Int = 0

    @Column(columnName = "received", columnType = ColumnType.INTEGER)
    @NotNull
    var received: Int = 0
}