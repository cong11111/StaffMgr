package com.loan.staffmgr.bean

class TicketsResponse {

    var ticket : Ticket? = null
    var contacts : List<Contact>? = null
    var va_link : String? = null

    class Ticket {
        var app_name : String? = null
        var ticket_id : Long? = null
        var name : String? = null
        var case_level : String? = null
        var address : String? = null
        var order_id : Long? = null
        var due_date : String? = null
        var due_days : Int? = null
        var repay_amount : Int? = null
        var principal : Int? = null
        var penalty : Int? = null
    }


    class Contact {
        var key : Int? = null
        var flag : String? = null
        var take : Int? = null
        var collect_count : Int? = null
        var mobile : String? = null
        var relationship : String? = null
    }
}