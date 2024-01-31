package com.loan.staffmgr.bean

class SaveLogRequest {

    var ticket_id: Long? = null
    var result: String? = null
    var remark: String? = null
    var communication_way: Int? = null

    var phone_time: String? = null

    var phone_objects: Int? = null
    var phone_connected: Int? = null
    var phone_object_mobile: String? = null

    // 还款意愿 1有, 2无
    var repay_inclination: Int? = null
    var promise_repay_time: String? = null

    var overdue_reason_item: Int? = null

    /**
     * {
    "ticket_id": 353324,
    "result": "Willing to pay",
    "remark": "test",
    "communication_way": 2,
    "phone_time": "2024-01-22 12:22:12",
    "phone_object": 1,
    "phone_connected": 1,
    "phone_object_mobile": "2341111155555",
    "repay_inclination": 1,
    "promise_repay_time": "2024-01-22 20:00",
    "overdue_reason_item": 2

    }
     */

//    "ticket_id":353346,"result":"Willing to pay","remark":"发个公告","communication_way":2,"phone_time":"2024-01-31 12:28:23","phone_objects":1,"phone_connected":0,"repay_inclination":1,"promise_repay_time":"2024-02-01 00:35:44"
}