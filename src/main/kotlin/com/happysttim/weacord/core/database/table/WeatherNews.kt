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
)
