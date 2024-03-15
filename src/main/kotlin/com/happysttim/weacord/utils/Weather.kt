package com.happysttim.weacord.utils

object Weather {

    fun typeToName(type: Int): String? {
        return when(type) {
            1 -> "강풍"
            2 -> "호우"
            3 -> "한파"
            4 -> "건조"
            5 -> "폭풍해일"
            6 -> "풍량"
            7 -> "태풍"
            8 -> "대설"
            9 -> "황사"
            12 -> "폭염"
            else -> null
        }
    }

    fun stressToName(type: Int): String? {
        return when(type) {
            0 -> "주의보"
            1 -> "경보"
            else -> null
        }
    }

    fun commandToName(type: Int): String? {
        return when(type) {
            1 -> "발표"
            2 -> "해제"
            3 -> "연장"
            6 -> "정정"
            7 -> "변경발표"
            8 -> "변경해제"
            else -> null
        }
    }

    fun cancelToName(type: Int): String? {
        return when(type) {
            0 -> "정상"
            1 -> "취소"
            else -> null
        }
    }

}