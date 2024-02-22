package com.loan.staffmgr.callphone

import android.app.Service
import android.os.Build
import android.telephony.PhoneStateListener

import android.telephony.TelephonyManager
import android.util.Log
import com.loan.staffmgr.collect.ReportCallLogMgr
import com.loan.staffmgr.global.App
import com.loan.staffmgr.utils.log.LogSaver

object PhoneStateObserver {

    var isOffHook = false

    fun addReceiveMsg() {
        Log.e("Okhttp", "addReceiveMsg")
        val telMng = App.mContext!!.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Api29Observer.addReceiveMsg(telMng)
        } else {
            telMng.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    when (state) {
                        TelephonyManager.CALL_STATE_IDLE ->
                            if (isOffHook) {
                                hookOff()
                                isOffHook = false
                            }
                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            Log.d("log", "接听")
                            isOffHook = true
                        }
                        TelephonyManager.CALL_STATE_RINGING -> {
                            Log.d("log", "响铃,来电号码：${phoneNumber}")
                        }
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    fun removeReceiveMsg() {
        isOffHook = false
        val telMng = App.mContext!!.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Api29Observer.removeReceiveMsg(telMng)
        } else {

        }
    }

    fun hookOff() {
        LogSaver.logToFile("hand up the phone  ")
        ReportCallLogMgr.readAndUpload()
    }

}