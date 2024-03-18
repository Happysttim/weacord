package com.happysttim.weacord.core.database.table

import com.happysttim.weacord.core.database.annotation.*

@Table("WeatherNews")
data class WeatherNews(
    @Column(columnName = "tmFc", columnType = ColumnType.STRING)
    @NotNull
    var tmFc: Long = 0,

    @Column(columnName = "stnId", columnType = ColumnType.INTEGER)
    @NotNull
    var stnId: Int = 0,

    @Column(columnName = "ann", columnType = ColumnType.TEXT)
    @NotNull
    var ann: String = "",
) {
    @Column(columnName = "id", columnType = ColumnType.INTEGER, autoIncrement = true)
    @PrimaryKey
    var id: Int = 0

    @Column(columnName = "received", columnType = ColumnType.INTEGER)
    @NotNull
    var received: Int = 0
}
