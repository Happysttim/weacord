package com.happysttim.weacord.core.data

import com.google.gson.annotations.SerializedName

data class Cd (
    @SerializedName("response")
    val message: Message
) {
    data class Message(
        @SerializedName("header")
        val header: Header,
        @SerializedName("body")
        val body: Body?
    ) {
        data class Header(
            @SerializedName("resultCode")
            val resultCode: String = "",
            @SerializedName("resultMsg")
            val resultMsg: String = ""
        )

        data class Body (
            @SerializedName("items")
            val items: Items,
            @SerializedName("pageNo")
            val page: Int,
            @SerializedName("totalCount")
            val totalCount: Int
        ) {
            data class Items (
                @SerializedName("item")
                val itemList: List<Item>
            ) {
                data class Item (
                    @SerializedName("stnId")
                    val stnId: Int = 0,

                    @SerializedName("tmFc")
                    val tmFc: Long = 0,

                    @SerializedName("tmSeq")
                    val tmSeq: Int = 0,

                    @SerializedName("allEndTime")
                    val allEndTime: Long = 0,

                    @SerializedName("areaCode")
                    val areaCode: String = "",

                    @SerializedName("areaName")
                    val areaName: String = "",

                    @SerializedName("cancel")
                    val cancel: Int = 0,

                    @SerializedName("command")
                    val command: Int = 0,

                    @SerializedName("endTime")
                    val endTime: Long = 0,

                    @SerializedName("warnVar")
                    val warnVar: Int = 0,

                    @SerializedName("warnStress")
                    val warnStress: Int = 0,

                    @SerializedName("startTime")
                    val startTime: Long = 0
                )
            }
        }
    }
}