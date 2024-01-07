package com.loan.staffmgr.global

object Api {

       private const val HOST = "http://dcapi.chucard.com"

       const val SEND_SMS = "$HOST/v1/admin/captcha"

       const val LOGIN = "$HOST/v1/admin/login"

       const val DASH_BOARD = "$HOST/v1/home/dashboard"

}