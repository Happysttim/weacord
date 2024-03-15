package com.happysttim.weacord.core.data

import com.google.gson.annotations.SerializedName

data class ContentTitle (
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
                val items: List<Item>
            ) {
                data class Item (
                    @SerializedName("stnId")
                    val stnId: Int = 0,

                    @SerializedName("tmFc")
                    val tmFc: Long = 0,

                    @SerializedName("tmSeq")
                    val tmSeq: Int = 0,

                    @SerializedName("title")
                    val title: String = ""
                )
            }
        }
    }
}