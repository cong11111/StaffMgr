package com.loan.staffmgr.bean

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy

class BaseResponseBean {

    private var data: Any? = null
    private var message: String? = null
    private var code: Int? = null

    fun isRequestSuccess(): Boolean {
        return code != null && code == 200
    }

    fun isLogout() : Boolean {
        if (code != null){
           if (code == 401 || code == 405){
               return true
           }
        }
        return false
    }

    fun getMessage(): String? {
        return message
    }

    fun getData(): Any? {
        return data
    }

    fun getBodyStr(): String? {
        val gson = GsonBuilder() //             # 将DEFAULT改为STRING
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .serializeNulls().create()
        return gson.toJson(data)
    }
}