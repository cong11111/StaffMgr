package com.loan.staffmgr.collect

import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import android.util.Log
import com.blankj.utilcode.util.ThreadUtils
import com.loan.staffmgr.global.App
import java.util.ArrayList

object CollectRecordLogMgr {
    const val TAG = "CollectRecordLogMgr"

    val mCallRecordList = ArrayList<CallLogRecord>()

    fun readCallRecordInNewThread() {
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<ArrayList<CallLogRecord>?>() {
            override fun doInBackground(): ArrayList<CallLogRecord>? {
                if (App.mContext != null) {
                    return readCallRecord(App.mContext!!)
                }
                return null
            }

            override fun onSuccess(result: ArrayList<CallLogRecord>?) {
                setData(result)
            }

        })
    }

    fun setData(result: ArrayList<CallLogRecord>?) {
        if (result != null) {
            mCallRecordList.clear()
            mCallRecordList.addAll(result)
        }
    }

    fun readCallRecord(context: Context): ArrayList<CallLogRecord> {
        val callRecordList = ArrayList<CallLogRecord>()
        val cr = context.contentResolver
        val uri = CallLog.Calls.CONTENT_URI
        val projection = arrayOf(
            CallLog.Calls.NUMBER, CallLog.Calls.DATE,
            CallLog.Calls.TYPE, CallLog.Calls.DURATION
        )
        var cursor: Cursor? = null
        try {
            cursor = cr.query(uri, projection, null, null, null)
            while (cursor!!.moveToNext()) {
                val number = cursor.getString(0)
                val date = cursor.getLong(1)
                val type = cursor.getInt(2)
                val duration = cursor.getInt(3)
                callRecordList.add(CallLogRecord(number, date, type, duration))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "read cardCord exception = $e")
        } finally {
            cursor?.close()
        }
        return callRecordList
    }

}