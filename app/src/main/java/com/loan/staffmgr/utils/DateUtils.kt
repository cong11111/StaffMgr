package com.loan.staffmgr.utils

import java.text.SimpleDateFormat

object MyDateUtils {

    fun convertStrToMillions(dateStr : String) : Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.parse(dateStr).time
    }

    fun reverseStr(dateStr : String) : String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val time = sdf.parse(dateStr).time
        val sdf1 = SimpleDateFormat("MM/dd/yyyy")
        return sdf1.format(time)
    }
}