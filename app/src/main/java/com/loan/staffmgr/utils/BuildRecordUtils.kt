package com.loan.staffmgr.utils

import android.provider.CallLog
import android.text.TextUtils
import android.util.Pair
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.CallLogRequest
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.collect.CollectRecordLogMgr
import com.loan.staffmgr.global.App
import com.loan.staffmgr.global.ConfigMgr
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object BuildRecordUtils {

    fun buildTargetList(list : ArrayList<TicketsResponse.Contact>) : ArrayList<Pair<String, String>> {
        val result = ArrayList<Pair<String, String>>()

        val contactList = java.util.ArrayList<TicketsResponse.Contact>()
        contactList.addAll(list)
        for (index in 0 until contactList.size) {
            val contact = contactList[index]
            if (!TextUtils.isEmpty(contact.mobile)) {
                result.add(Pair(contact.relationship + " _ " + contact.mobile!!, contact.mobile!!))
            }
        }
        return result
    }

    fun getRelativeShip(flag : String?) : Int{
        try {
            for (index in 0 until ConfigMgr.mPhoneObject.size) {
                val pair = ConfigMgr.mPhoneObject[index]
                if (TextUtils.equals(pair.first, flag)) {
                    return pair.second.toInt()
                }
            }
        } catch (e : java.lang.Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            }
        }

        return 1
    }

    fun buildWhatAppList(list : ArrayList<TicketsResponse.Contact>) : ArrayList<Pair<String, String>> {
        val result = ArrayList<Pair<String, String>>()

        val contactList = java.util.ArrayList<TicketsResponse.Contact>()
        contactList.addAll(list)
        for (index in 0 until contactList.size) {
            val contact = contactList[index]
            if (!TextUtils.isEmpty(contact.mobile)) {
                result.add(Pair(contact.flag + " _ "+ contact.relationship, contact.flag!!))
            }
        }
        return result
    }

    fun buildCallTimeList(list : ArrayList<CallLogRequest>) : ArrayList<Pair<String, String>>{
        val result = ArrayList<Pair<String, String>>()

        val callLogRecordList = ArrayList<CallLogRequest>()
        callLogRecordList.addAll(list)
        for (index in 0 until callLogRecordList.size) {
            val callLogRecord = callLogRecordList[index]
            if (callLogRecord.call_time != null) {
                val dateStr = callLogRecord.call_time!!
                val first = getRingStateStr(callLogRecord) + "  " + dateStr
                result.add(Pair(first, callLogRecord.call_time!!.toString()))
            }
        }
        return result
    }

    private fun getRingStateStr(callLogRecord : CallLogRequest) : String {
        if (App.mContext == null) {
            return " not exist"
        }
        val resources = App.mContext!!.resources
        if (callLogRecord.duration != null && callLogRecord.duration!! > 0) {
            return resources.getString(R.string.take)
        } else {
            return resources.getString(R.string.ring)
        }
    }

    fun convertMillionToStr(dateMillions : Long) : String {
        val date = Date()
        date.time = dateMillions
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val datef = sdf.format(date)
        return datef
    }

    fun buildCallResultList() : ArrayList<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()


        if (App.mContext != null) {
            //        if (ConfigMgr.mResult.isNotEmpty()) {
//            for (index in 0 until ConfigMgr.mResult.size) {
//                val dueResult = ConfigMgr.mResult[index]
//                list.add(Pair(dueResult.Lang, dueResult.Id.toString()))
//            }
//            return list
//        }
        }
        val resources = App.mContext!!.resources
        val str2 = resources.getString(R.string.ring)
        val str3 = resources.getString(R.string.does_not_exist)
        val str4 = resources.getString(R.string.out_of_service)

        list.add(Pair(str2, "2"))
        list.add(Pair(str3, "3"))
        list.add(Pair(str4, "4"))
        return list
    }

    fun buildFeedbackList() : ArrayList<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        if (ConfigMgr.mResult.isNotEmpty()) {
            for (index in 0 until ConfigMgr.mResult.size) {
                val dueResult = ConfigMgr.mResult[index]
                list.add(Pair(dueResult.Lang, dueResult.Id.toString()))
            }
            return list
        }

        if (App.mContext == null) {
            return list
        }
        val resources = App.mContext!!.resources
        val str1 = resources.getString(R.string.willing_to_pay)
        val str2 = resources.getString(R.string.no_willing)
        val str3 = resources.getString(R.string.not_customer)
        val str4 = resources.getString(R.string.not_receive_money)
        val str5 = resources.getString(R.string.already_paid_but_delay)
        val str6 = resources.getString(R.string.partial_pay_first)
        val str7 = resources.getString(R.string.not_pick)

        list.add(Pair(str1, "1"))
        list.add(Pair(str2, "2"))
        list.add(Pair(str3, "3"))
        list.add(Pair(str4, "4"))
        list.add(Pair(str5, "5"))
        list.add(Pair(str6, "6"))
        list.add(Pair(str7, "7"))
        return list
    }

    fun getRecordByTarget(list : ArrayList<CallLogRequest>, date : String) : CallLogRequest?{
        for (index in 0 until list.size) {
            val callLogRecord = list[index]
            if (TextUtils.equals(callLogRecord.call_time.toString(), date) ){
                return callLogRecord
            }
        }
        return null
    }

    fun getCallCount(num : String) : Pair<Int, Int> {
        var totalCount : Int = 0
        var hasCalledCount : Int = 0
        for (index in 0 until CollectRecordLogMgr.mCallRecordList.size) {
            val callLogRecord = CollectRecordLogMgr.mCallRecordList[index]
            if (TextUtils.equals(callLogRecord.num, num) &&
                callLogRecord.type == CallLog.Calls.OUTGOING_TYPE
//                && TextUtils.equals(callLogRecord.date.toString(), date)
            ){
                totalCount++
                if (callLogRecord.duration != null && callLogRecord.duration!! > 0) {
                    hasCalledCount++
                }
            }
        }

        return Pair<Int, Int>(hasCalledCount, totalCount)

    }

//    fun getRecordByTargetList(desc : String, num : String) : ArrayList<CallLogRecord>{
//        val list = ArrayList<CallLogRecord>()
//        for (index in 0 until CollectRecordLogMgr.mCallRecordList.size) {
//            val callLogRecord = CollectRecordLogMgr.mCallRecordList[index]
//            if (TextUtils.equals(callLogRecord.num, num ) &&
//                callLogRecord.type == CallLog.Calls.OUTGOING_TYPE
////                && TextUtils.equals(callLogRecord.date.toString(), date)
//            ){
//                list.add(callLogRecord)
//            }
//        }
//        Collections.sort(list, object : Comparator<CallLogRecord> {
//            override fun compare(t1: CallLogRecord, t2: CallLogRecord): Int {
//                try {
//                    val first = t1.date
//                    val second = t2.date
//                    if (first != null && second != null) {
//                        return (first - second).toInt()
//                    }
//                    return 1
//                } catch (e: Exception) {
//                }
//                return 1
//            }
//        })
//        return list
//    }
}