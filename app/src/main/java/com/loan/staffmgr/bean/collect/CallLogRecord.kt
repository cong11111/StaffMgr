package com.loan.staffmgr.bean.collect

class CallLogRecord {

    var num : String? = null
    var date : Long? = null
    var type : Int? = null
    var duration : Int? = null
    var desc : String? = null

    constructor(num : String, date : Long, type : Int, duration : Int){
        this.num = num
        this.date = date
        this.type = type
        this.duration = duration
    }
}