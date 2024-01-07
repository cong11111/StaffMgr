package com.example.myapplication.log

import android.content.Context
import java.io.File
import java.util.*


internal const val logPre = "log"
internal const val logTotalDir = "/log/"
internal const val logFileNameDiv = "_"
internal const val mainProDir = "mainP/"
internal const val oneMSize = 1024 * 1024
//手机存储空间最小要求,小于10MB不可用
internal const val minSpaceNeed = 10 * oneMSize

internal class LogWriterConfig {

    val calendar: Calendar = Calendar.getInstance()
    //每次扩容100KB
    val increaseSpace = oneMSize * 1L / 10

    //每个日志文件最大size：1MB
    val maxSizePerFile = oneMSize

    //当前生效中的log文件,文件命名规则：log_递增数字_时间戳
    @Volatile var curValidLogFile: File? = null

    //最大可用空间，单位：MB,每个文件1MB
    var maxLogSize = 5
    var appContext: Context? = null
    //当前进程日志目录
    var processDir = mainProDir

    //当前进程日志文件的所属文件夹
    var logFileFolder: String? = null
    //当前应用的日志目录（所有进程公用）
    var totalFileFolder: String? = null
}