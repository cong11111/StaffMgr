package com.loan.staffmgr.bean

import com.loan.staffmgr.bean.collect.CallLogRecord
import com.loan.staffmgr.utils.BuildRecordUtils

class CallLogRequest {

    var mobile : String? = null
    var call_time : String? = null
    var duration : String? = null
    var type : Int? = null
    var desc : String? = null

    constructor(callLog : CallLogRecord, desc : String? = null) {
        mobile = callLog.num
        if (callLog.date != null) {
            call_time = BuildRecordUtils.convertMillionToStr(callLog.date!!)
        }
        if (callLog.duration != null) {
            duration = callLog.duration!!.toString()
        }
        if (callLog.type != null) {
            type = callLog.type!!
        }
        this.desc = desc
    }
}