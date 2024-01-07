package com.example.myapplication.log

import android.util.Log
internal const val logTag = "LogSaver"

internal object MsgPrinter {
    var enable = false
    fun logE(content: String) {
        if (!enable) {
            return
        }
        Log.e(logTag, content)
    }

    fun logD(content: String) {
        if (!enable) {
            return
        }
        Log.d(logTag, content)
    }
}