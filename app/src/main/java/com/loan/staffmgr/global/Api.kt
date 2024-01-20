package com.loan.staffmgr.global

object Api {

       private const val HOST = "http://dcapi.chucard.com"
       //获取登录验证码
       const val SEND_SMS = "$HOST/v1/admin/captcha"
       // 登录获取token
       const val LOGIN = "$HOST/v1/admin/login"
       //首页Dashboard
       const val DASH_BOARD = "$HOST/v1/home/dashboard"
       //首页Tickets
       const val TICKETS = "$HOST/v1/home/tickets"
       //获取催收记录
       const val TICKET_LOG_LIST = "$HOST/v1/ticket/log/list"
       //保存催收记录
       const val TICKET_LOG_ADD = "$HOST/v1/ticket/log/add"

}