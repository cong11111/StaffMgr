package com.loan.staffmgr.collect

import android.os.Build
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.ThreadUtils
import com.loan.staffmgr.bean.CallLogRequest
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.global.App
import com.loan.staffmgr.utils.CheckResponseUtils
import com.loan.staffmgr.utils.log.LogSaver
import com.lzy.okgo.BuildConfig
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response

object ReportCallLogMgr {

    val TAG = "ReportCallLogMgr"

    val mSet =  HashSet<String>()
    val mListeners =  HashSet<CallBack>()

    private var isUploading : Boolean = false

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
            executeOnEnd(false," No need upload.")
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
        LogSaver.logToFile("upload call log size = " + tempList.size)
//        Log.e("Test", " upload log")
        OkGo.post<String>(Api.RECORD_ADD).tag(TAG)
            .upJson(com.alibaba.fastjson.JSONArray.toJSONString(tempList))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val responseStr = CheckResponseUtils.checkResponseSuccess(response)
                    executeOnEnd(true)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    executeOnEnd(false)
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

    fun readAndUpload(){
        executeOnStart()
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<java.util.ArrayList<CallLogRecord>?>() {
            override fun doInBackground(): java.util.ArrayList<CallLogRecord>? {
                if (com.loan.staffmgr.BuildConfig.DEBUG) {
                    SystemClock.sleep(5000)
                }
                if (App.mContext != null) {
                    return CollectRecordLogMgr.readCallRecord(App.mContext!!)
                }
                return null
            }

            override fun onSuccess(result: java.util.ArrayList<CallLogRecord>?) {
                if (result == null) {
                    executeOnEnd(false)
                    return
                }
                CollectRecordLogMgr.setData(result)
                uploadCallLog()
            }

        })
    }

    interface CallBack {
        fun onStart()

        fun onEnd(isSuccess : Boolean, desc: String?)
    }

    private fun executeOnStart() {
        isUploading = true
        val iterator = mListeners.iterator()
        while (iterator.hasNext()){
            val next = iterator.next()
            next.onStart()
        }
    }

    private fun executeOnEnd(isSuccess : Boolean , desc : String? = null) {
        isUploading = false
        val iterator = mListeners.iterator()
        while (iterator.hasNext()){
            val next = iterator.next()
            next.onEnd(isSuccess, desc)
        }
    }

    fun addCallBack(callBack: CallBack) {
        mListeners.add(callBack)
    }

    fun removeCallBack(callBack: CallBack) {
        mListeners.remove(callBack)
    }

    fun removeAll() {
        mListeners.clear()
    }
}