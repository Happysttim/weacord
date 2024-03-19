package com.happysttim.weacord.core.database.table

import com.happysttim.weacord.core.database.annotation.*

@Table("NationalBreakNews")
data class NationalBreakNews (
    @Column(columnName = "tmSeq", columnType = ColumnType.INTEGER)
    @NotNull
    var tmSeq: Int = 0,

    @Column(columnName = "tmFc", columnType = ColumnType.STRING)
    @NotNull
    var tmFc: Long = 0,

    @Column(columnName = "stnId", columnType = ColumnType.INTEGER)
    @NotNull
    var stnId: Int = 0,

    @Column(columnName = "title", columnType = ColumnType.STRING)
    @NotNull
    var title: String = ""
)