package com.loan.staffmgr.boardcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class PhoneBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        if (intent.action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            Log.e("Test", "in come")
        }
    }
}