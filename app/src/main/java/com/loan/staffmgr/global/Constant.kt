package com.loan.staffmgr.global

import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.bean.DashboardResponse


object Constant {

    const val KEY_ACCOUNT = "key_sign_in_account"
    const val KEY_PASS_CODE = "key_sign_in_pass_code"
    const val KEY_TOKEN = "key_sign_in_token"
    const val KEY_EXPIRE = "key_sign_in_expire"



    var mToken : String? = null
    var mDashBoard : DashboardResponse? = null

}