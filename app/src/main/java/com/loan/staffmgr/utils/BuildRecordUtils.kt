package com.loan.staffmgr.utils

import android.provider.CallLog
import android.text.TextUtils
import android.util.Pair
import androidx.annotation.VisibleForTesting
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.bean.collect.CallLogRecord
import com.loan.staffmgr.collect.CollectRecordLogMgr
import com.loan.staffmgr.global.App
import com.loan.staffmgr.global.AppManager
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
                result.add(Pair(contact.flag + " _ "+ contact.relationship + " _ " + contact.mobile!!, contact.mobile!!))
            }
        }
        return result
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

    fun buildCallTimeList(list : ArrayList<CallLogRecord>) : ArrayList<Pair<String, String>>{
        val result = ArrayList<Pair<String, String>>()

        val callLogRecordList = ArrayList<CallLogRecord>()
        callLogRecordList.addAll(list)
        for (index in 0 until callLogRecordList.size) {
            val callLogRecord = callLogRecordList[index]
            if (callLogRecord.date != null) {
                val dateStr = convertMillionToStr(callLogRecord.date!!)
                val first = getRingStateStr(callLogRecord) + "  " + dateStr
                result.add(Pair(first, callLogRecord.date!!.toString()))
            }
        }
        return result
    }

    private fun getRingStateStr(callLogRecord : CallLogRecord) : String {
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

    fun buildFeedbackList() : ArrayList<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
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

        list.add(Pair(str1, str1))
        list.add(Pair(str2, str2))
        list.add(Pair(str3, str3))
        list.add(Pair(str4, str4))
        list.add(Pair(str5, str5))
        list.add(Pair(str6, str6))
        list.add(Pair(str7, str7))
        return list
    }

    fun getRecordByTarget(list : ArrayList<CallLogRecord>, date : String) : CallLogRecord?{
        for (index in 0 until list.size) {
            val callLogRecord = list[index]
            if (TextUtils.equals(callLogRecord.date.toString(), date) ){
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

    fun getRecordByTargetList(desc : String, num : String) : ArrayList<CallLogRecord>{
        val list = ArrayList<CallLogRecord>()
        for (index in 0 until CollectRecordLogMgr.mCallRecordList.size) {
            val callLogRecord = CollectRecordLogMgr.mCallRecordList[index]
            if (TextUtils.equals(callLogRecord.num, num ) &&
                callLogRecord.type == CallLog.Calls.OUTGOING_TYPE
//                && TextUtils.equals(callLogRecord.date.toString(), date)
            ){
                list.add(callLogRecord)
            }
        }
        Collections.sort(list, object : Comparator<CallLogRecord> {
            override fun compare(t1: CallLogRecord, t2: CallLogRecord): Int {
                try {
                    val first = t1.date
                    val second = t2.date
                    if (first != null && second != null) {
                        return (first - second).toInt()
                    }
                    return 1
                } catch (e: Exception) {
                }
                return 1
            }
        })
        return list
    }
}