package com.loan.staffmgr.bean

import com.loan.staffmgr.collect.CallLogRecord
import com.loan.staffmgr.utils.BuildRecordUtils

class CallLogRequest {

    var mobile : String? = null
    var call_time : String? = null
    var duration : String? = null
    var type : Int? = null
    var desc : String? = null

    companion object {
        fun buildData(callLog : CallLogRecord, desc : String? = null) : CallLogRequest {
            val callLogRequest = CallLogRequest()
            callLogRequest.mobile = callLog.num
            if (callLog.date != null) {
                callLogRequest.call_time = BuildRecordUtils.convertMillionToStr(callLog.date!!)
            }
            if (callLog.duration != null) {
                callLogRequest.duration = callLog.duration!!.toString()
            }
            if (callLog.type != null) {
                callLogRequest.type = callLog.type!!
            }
            callLogRequest.desc = desc
            return callLogRequest
        }
    }
}