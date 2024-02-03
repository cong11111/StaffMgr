package com.loan.staffmgr.bean

class TicketsResponse {

    var ticket : Ticket? = null
    var contacts : List<Contact>? = null
    var va_link : String? = null

    class Ticket {
        var app_name : String? = null
        var ticket_id : Long? = null
        var name : String? = null

        // TicketStatusCreated          TicketStatusEnum = 0
        //TicketStatusAssigned         TicketStatusEnum = 1
        //TicketStatusProcessing       TicketStatusEnum = 3
        //TicketStatusCompleted        TicketStatusEnum = 4
        //TicketStatusClosed           TicketStatusEnum = 5
        //TicketStatusPartialCompleted TicketStatusEnum = 6
        //TicketStatusWaitingEntrust   TicketStatusEnum = 7

        var status : Int? = null
        var promise_repay_date : String? = null
        var last_collection_time : String? = null
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