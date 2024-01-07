package com.loan.staffmgr.bean

class DashboardResponse {
    var account : Account? = null
    var collection : Collection? = null

    class Account {
        var group : String? = null
        var name : String? = null
    }

    class Collection {
        var assign_amount : Amount? = null
        var current_rank : Rank? = null
        var repay_amount : Amount? = null
        var repay_rate : Amount? = null
    }

    class Amount {
        var first_loan : String? = null
        var reloan : String? = null

    }

    class Rank {
        var rank : String? = null
    }
}