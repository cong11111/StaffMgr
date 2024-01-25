package com.loan.staffmgr.global

import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.bean.DashboardResponse
import com.loan.staffmgr.bean.TicketsResponse


object Constant {

    const val KEY_ACCOUNT = "key_sign_in_account"
    const val KEY_PASS_CODE = "key_sign_in_pass_code"
    const val KEY_TOKEN = "key_sign_in_token"
    const val KEY_EXPIRE = "key_sign_in_expire"



    var mToken : String? = null
    var mName : String? = null
    var mDashBoard : DashboardResponse? = null

    var mTicketLists : ArrayList<TicketsResponse> = ArrayList()

    // TicketStatusCreated          TicketStatusEnum = 0
    //TicketStatusAssigned         TicketStatusEnum = 1
    //TicketStatusProcessing       TicketStatusEnum = 3
    //TicketStatusCompleted        TicketStatusEnum = 4
    //TicketStatusClosed           TicketStatusEnum = 5
    //TicketStatusPartialCompleted TicketStatusEnum = 6
    //TicketStatusWaitingEntrust   TicketStatusEnum = 7
    var mTicketStatus : Int = -1
}