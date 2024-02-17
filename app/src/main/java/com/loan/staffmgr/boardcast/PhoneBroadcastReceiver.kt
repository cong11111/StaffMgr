package com.loan.staffmgr.boardcast

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.loan.staffmgr.collect.ReportCallLogMgr
import com.loan.staffmgr.global.App
import com.loan.staffmgr.utils.log.LogSaver


class PhoneBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private var isOffHook = false

        fun addReceiveMsg() {
            Log.e("Okhttp", "addReceiveMsg")
            val telMng = App.mContext!!.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                telMng.registerTelephonyCallback(
                    App.mContext!!.mainExecutor, mCallback)
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
                telMng.unregisterTelephonyCallback(mCallback)
            } else {

            }
        }

        private fun hookOff() {
            LogSaver.logToFile("hand up the phone  ")
            ReportCallLogMgr.readAndUpload()
        }

        private val mCallback = @RequiresApi(Build.VERSION_CODES.S)
        object : TelephonyCallback(), TelephonyCallback.CallStateListener {
            override fun onCallStateChanged(state: Int) {
                when (state) {
                    TelephonyManager.CALL_STATE_IDLE -> {
                        Log.d("Okhttp", "挂断")
                        if (isOffHook) {
                            hookOff()
                            isOffHook = false
                        }
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        Log.d("Okhttp", "接听")
                        isOffHook = true

                    }
                    TelephonyManager.CALL_STATE_RINGING -> {
                        Log.d("Okhttp", "CALL_STATE_RINGING")

                    }
                }


            }
        }

    }



    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        if (intent.action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            Log.e("Test", "in come")
        }
    }
}