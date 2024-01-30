package com.loan.staffmgr.boardcast

import android.Manifest.permission.PROCESS_OUTGOING_CALLS
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import com.loan.staffmgr.collect.ReportCallLogMgr
import com.loan.staffmgr.global.App


class PhoneBroadcastReceiver : BroadcastReceiver() {

    private var isOffHook = false

    init {
        val telMng = App.mContext!!.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telMng.registerTelephonyCallback(
                App.mContext!!.mainExecutor,
                object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                    override fun onCallStateChanged(state: Int) {
                        when (state) {
                            TelephonyManager.CALL_STATE_IDLE -> {
                                Log.d("pkg", "挂断")
                                if (isOffHook) {
                                    ReportCallLogMgr.uploadCallLog()
                                    isOffHook = false
                                }
                            }
                            TelephonyManager.CALL_STATE_OFFHOOK -> {
                                Log.d("pkg", "接听")
                                isOffHook = true

                            }
                            TelephonyManager.CALL_STATE_RINGING -> {
                                Log.d("pkg", "CALL_STATE_RINGING")

                            }
                        }


                    }
                })
        } else {
            telMng.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    when (state) {
                        TelephonyManager.CALL_STATE_IDLE ->
                            Log.d("log", "挂断")
                        TelephonyManager.CALL_STATE_OFFHOOK ->
                            Log.d("log", "接听")
                        TelephonyManager.CALL_STATE_RINGING -> {
                            Log.d("log", "响铃,来电号码：${phoneNumber}")
                        }
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
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