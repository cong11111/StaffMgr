package com.loan.staffmgr.callphone
import android.os.Build
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.loan.staffmgr.global.App

@RequiresApi(Build.VERSION_CODES.S)
object Api29Observer {

    fun addReceiveMsg(mgr : TelephonyManager) {
        mgr.registerTelephonyCallback(
            App.mContext!!.mainExecutor, mCallback)
    }

    fun removeReceiveMsg(mgr : TelephonyManager) {
        mgr.unregisterTelephonyCallback(mCallback)
    }
    private val mCallback =
    object : TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                    Log.d("Okhttp", "挂断")
                    if (PhoneStateObserver.isOffHook) {
                        PhoneStateObserver.hookOff()
                        PhoneStateObserver.isOffHook = false
                    }
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    Log.d("Okhttp", "接听")
                    PhoneStateObserver.isOffHook = true

                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d("Okhttp", "CALL_STATE_RINGING")

                }
            }


        }
    }

}