package com.loan.staffmgr.utils

import android.text.TextUtils
import android.util.Pair
import androidx.annotation.VisibleForTesting
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.collect.CallLogRecord
import com.loan.staffmgr.collect.CollectRecordLogMgr
import com.loan.staffmgr.global.App

object BuildRecordUtils {

    @VisibleForTesting
    fun buildTargetListTest() : ArrayList<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()

        val callRecordList = java.util.ArrayList<CallLogRecord>()
        callRecordList.addAll( CollectRecordLogMgr.mCallRecordList)
        for (index in 0 until callRecordList.size) {
            val callRecord = callRecordList[index]
            if (!TextUtils.isEmpty(callRecord.num) && (
                        callRecord.num!!.contains("18939440244") ||
                                callRecord.num!!.contains("18539253050")

                        )) {
                if (!TextUtils.isEmpty(callRecord.num)) {
                    list.add(Pair(callRecord.num, callRecord.date.toString()))
                }
            }
        }
        return list
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
}