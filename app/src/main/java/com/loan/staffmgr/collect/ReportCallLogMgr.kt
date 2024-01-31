package com.loan.staffmgr.collect

import android.text.TextUtils
import android.util.Log
import com.loan.staffmgr.bean.CallLogRequest
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.utils.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response

object ReportCallLogMgr {

    val TAG = "ReportCallLogMgr"

    val mSet =  HashSet<String>()

    fun uploadCallLog() {
        val resultList: ArrayList<CallLogRecord> = ArrayList()
        for (index in 0 until CollectRecordLogMgr.mCallRecordList.size) {
            val callLogRequest = CollectRecordLogMgr.mCallRecordList[index]
            if ((mSet.isEmpty() || mSet.contains(callLogRequest.num)) &&
                isValidDate(callLogRequest.date)) {
                resultList.add(callLogRequest)
            }
        }
        if (resultList.isNotEmpty()) {
            realUploadCallLog(resultList)
        } else {
            Log.e("Okhttp", " no need upload,")
        }
    }

    private fun isValidDate(date : Long?) : Boolean{
        if (date == null){
            return true
        }
        if (date > System.currentTimeMillis()) {
            return true
        }
        if (System.currentTimeMillis() - date <= 2 * 24 * 60 * 60 * 1000) {
            return true
        }
        return false
    }

    private fun realUploadCallLog(list: List<CallLogRecord>) {
        val tempList = ArrayList<CallLogRequest>()
        for (index in 0 until list.size) {
            val callLogRequest = CallLogRequest.buildData(list[index])
            tempList.add(callLogRequest)
        }
//        Log.e("Test", " upload log")
        OkGo.post<String>(Api.RECORD_ADD).tag(TAG)
            .upJson(com.alibaba.fastjson.JSONArray.toJSONString(tempList))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val responseStr = CheckResponseUtils.checkResponseSuccess(response)

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                }
            })
    }

    fun setData(tickets: MutableList<TicketsResponse>) {
        val set = HashSet<String>()
        for (index in 0 until tickets.size) {
            val ticket = tickets[index]
            if (ticket.contacts == null) {
                continue
            }
            for (index2 in 0 until ticket.contacts!!.size) {
                val contact2 = ticket.contacts!![index2]
                if (!TextUtils.isEmpty(contact2.mobile)) {
                    set.add(contact2.mobile!!)
                }
            }
        }
        if (set.isNotEmpty()){
            mSet.clear()
            mSet.addAll(set)
        }
    }
}