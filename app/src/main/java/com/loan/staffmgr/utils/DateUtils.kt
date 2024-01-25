package com.loan.staffmgr.utils

import java.text.SimpleDateFormat

object MyDateUtils {

    fun convertStrToMillions(dateStr : String) : Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.parse(dateStr).time
    }
}