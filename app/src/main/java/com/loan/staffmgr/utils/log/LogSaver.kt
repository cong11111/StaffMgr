package com.loan.staffmgr.utils.log

import android.content.Context
import android.text.TextUtils
import com.example.myapplication.log.MsgPrinter
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.PrintWriter
import java.io.RandomAccessFile
import java.io.StringWriter
import java.nio.channels.FileChannel
import java.util.*
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.*
import java.lang.Runnable


object LogSaver {
    /**
     * 开启测试开关,与init无先后顺序
     */
    @JvmStatic
    fun enableDebug() {
        ConfigManager.isDebug = true
        MsgPrinter.enable = true
    }

    private fun exceptionToStr(e: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        return sw.toString()
    }

    /**
     * 获取日志的总目录（所有进程公用）
     */
    @JvmStatic
    fun getLogFileFolder(): String {
        return ConfigManager.totalFileFolder()
    }

    /**
     * @maxSize 当前进程最大存储空间，范围2～10，单位MB，默认4MB
     * 注意：所有进程日志空间总和不建议超过10MB，否则feedback时，
     * 可能导致超过邮箱附件的最大上线，导致邮箱发送失败。
     */
    @JvmStatic
    fun init(context: Context, maxSize: Int = -1) {
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        if (oldHandler !is CrashHandlerForLog) {//防止重复init造成的oldHandler丢失
            Thread.setDefaultUncaughtExceptionHandler(object : CrashHandlerForLog() {
                override fun uncaughtException(t: Thread, e: Throwable) {
                    if (ConfigManager.isDebug) {
                        e.printStackTrace()
                    }
                    innerLogToFile(
                        "Thread:${t.name}_${t.id}==>${exceptionToStr(e)}",
                        getCurTimeStr(),
                        "activity"
                    )
                    if (oldHandler != null) {
                        MsgPrinter.logE("自定义异常handler，oldHandler")
                        oldHandler.uncaughtException(t, e)
                    }
                }
            })

        }
        ConfigManager.executor(Runnable {
            ConfigManager.init(context, maxSize)
        })
    }

    @JvmStatic
    fun logToFile(throwable: Throwable, cateName: String = "common", addTime: Boolean = true) {
        val currentTime = if (addTime) getCurTimeStr() else ""
        ConfigManager.executor(Runnable {
            innerLogToFile(exceptionToStr(throwable), currentTime, cateName)
        })
    }

    @JvmStatic
    fun logToFile(content: String) {
        logToFile(content, addTime = true)
    }


    @JvmStatic
    fun logToFile(content: String, cateName: String) {
        logToFile(content, cateName, true)
    }

    /**
     * @cateName 日志类别名称，建议越短越好，以节省空间
     */
    @JvmStatic
    fun logToFile(content: String, cateName: String = "common", addTime: Boolean = true) {
        val currentTime = if (addTime) getCurTimeStr() else ""
        ConfigManager.executor(Runnable {
            innerLogToFile(content, currentTime, cateName)
        })
    }

    /**
     * 读取fileName对应的内容
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @JvmStatic
    suspend fun getFileContentByName(fileName: String): String? {
        if (TextUtils.isEmpty(fileName)) {
            MsgPrinter.logE("getKVFileContent失败，key为空！")
            return null
        }
        return suspendCancellableCoroutine { continuation: CancellableContinuation<String?> ->
            try {
                val kvFileContent = getKFFilePath(fileName)
                if (!File(kvFileContent).exists()) {
                    MsgPrinter.logE("getKVFileContent失败，key对应的file不存在！")
                    continuation.resume("") {}
                    return@suspendCancellableCoroutine
                }
                val createRaf = createRaf(File(kvFileContent))
                if (createRaf == null) {
                    MsgPrinter.logE("getKVFileContent失败，RandomAccessFile创建失败。")
                    continuation.resume("") {}
                    return@suspendCancellableCoroutine
                }

                val channel = createRaf.channel
                val sizeMap = channel.map(FileChannel.MapMode.READ_ONLY, 0, Long.SIZE_BYTES * 1L)
                val saveSize = sizeMap.long
                val contentMap =
                    channel.map(FileChannel.MapMode.READ_ONLY, Long.SIZE_BYTES * 1L, saveSize)
                val result = Charsets.UTF_8.decode(contentMap)?.toString() ?: ""
                continuation.resume(result) {}
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    @JvmStatic
    private fun getKFFilePath(key: String): String {
        val kvPath = "${ConfigManager.totalFileFolder()}/kvContent/"
        return "$kvPath$key"
    }

    /**
     * 删除fileName对应的文件
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @JvmStatic
    suspend fun delFileByName(fileName: String) =
        suspendCancellableCoroutine { continuation: CancellableContinuation<Boolean> ->
            if (TextUtils.isEmpty(fileName)) {
                MsgPrinter.logE("delKVFile失败， key为空！")
                resumeContinuation(continuation, false)
                return@suspendCancellableCoroutine
            }
            ConfigManager.executor {
                try {
                    val kvFile = File(getKFFilePath(fileName))
                    if (kvFile.exists()) {
                        kvFile.delete()
                    }
                    resumeContinuation(continuation, true)
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }

        }

    /**
     * 每个fileName保存一个独立的文件,效率较低，不适用于频繁调用的场景
     * 每次保存会覆盖掉旧的内容
     * content 需要小于1MB
     * @return false：保存失败；true：成功
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @JvmStatic
    suspend fun saveContentToFileName(fileName: String, content: String) =
        suspendCancellableCoroutine { continuation: CancellableContinuation<Boolean> ->
            if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(content)) {
                MsgPrinter.logE("saveKVToFile失败， key 或者 value 为空！")
                resumeContinuation(continuation, false)
                return@suspendCancellableCoroutine
            }
            if (!ConfigManager.isMainProcess()) {
                MsgPrinter.logE("只有主进程才可以调用该API：saveContentToFileName")
                resumeContinuation(continuation, false)
                return@suspendCancellableCoroutine
            }
            ConfigManager.executor(Runnable {
                try {
                    if (!ConfigManager.isMobileEnoughSpace()) {
                        MsgPrinter.logE("当前手机空间不足10MB！取消保存。。。")
                        resumeContinuation(continuation, false)
                        return@Runnable
                    }
                    val kvFile = File(getKFFilePath(fileName))
                    if (kvFile.exists()) {
                        kvFile.delete()
                    }
                    val parentFile = kvFile.parentFile
                    if (parentFile == null) {
                        resumeContinuation(continuation, false)
                        return@Runnable
                    }
                    if (!parentFile.exists()) {
                        parentFile.mkdirs()
                    }
                    kvFile.createNewFile()
                    val createRaf = createRaf(kvFile)
                    if (createRaf == null) {
                        resumeContinuation(continuation, false)
                        return@Runnable
                    }
                    val toByteArray = content.toByteArray(Charsets.UTF_8)
                    if (ConfigManager.isOverSize(0, toByteArray.size)) {
                        //判断是否已超过单个文件的最大限制
                        if (ConfigManager.isDebug) {
                            throw RuntimeException("保存的内容总长度：${toByteArray.size}B,超过最大限制：${ConfigManager.oneFileMaxSize()}B")
                        }
                        resumeContinuation(continuation, false)
                        return@Runnable
                    }
                    val sizeBits = Long.SIZE_BYTES.toLong() + toByteArray.size
                    val channel = createRaf.channel
                    val mapContent = channel.map(FileChannel.MapMode.READ_WRITE, 0, sizeBits)
                    mapContent.putLong(toByteArray.size * 1L)
                    mapContent.put(toByteArray)
                    closeRaf(createRaf)
                    resumeContinuation(continuation, true)
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            })

        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun resumeContinuation(
        continuation: CancellableContinuation<Boolean>,
        result: Boolean
    ) {
        continuation.resume(result) {}
    }

    @JvmStatic
    private fun getCurTimeStr(): String {
        val calendar: Calendar =
            ConfigManager.getCalendar() ?: return "${System.currentTimeMillis()}"
        calendar.timeInMillis = System.currentTimeMillis()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
            calendar.get(
                Calendar.DAY_OF_MONTH
            )
        } ${calendar.get(Calendar.HOUR_OF_DAY)}:${
            calendar.get(
                Calendar.MINUTE
            )
        }:${calendar.get(Calendar.SECOND)}.${calendar.get(Calendar.MILLISECOND)}"
    }

    @JvmStatic
    private fun innerLogToFile(content: String, currentTime: String, cateName: String) {
        var fileToWriteIn = ConfigManager.getCurValidFile()
        if (fileToWriteIn == null) {
            MsgPrinter.logE("获取可写日志文件失败，请确认是否已初始化！")
            return
        }
        if (TextUtils.isEmpty(cateName)) {
            MsgPrinter.logE("日志的cateName不能为空！")
            return
        }
        if (!ConfigManager.isMobileEnoughSpace()) {
            MsgPrinter.logE("当前手机空间不足10MB！取消保存。。。")
            return
        }
        val timeContent = if (!TextUtils.isEmpty(currentTime)) "${currentTime}:" else ""
        val tempContent = "<S ${cateName}>${timeContent}${content}<E>\n"
        val byteToWrite = tempContent.toByteArray(Charsets.UTF_8)
        if (ConfigManager.isOverSize(0, byteToWrite.size)) {
            //判断是否已超过单个文件的最大限制
            if (ConfigManager.isDebug) {
                throw RuntimeException("保存的内容总长度：${byteToWrite.size}B,超过最大限制：${ConfigManager.oneFileMaxSize()}B")
            }
            return
        }
        if (!ConfigManager.bufferInit()) {
            val initResult = initBuffers(fileToWriteIn, byteToWrite)
            if (initResult == -1) {
                fileToWriteIn = switchFile(fileToWriteIn)
                //新文件再次尝试初始化
                initBuffers(fileToWriteIn, byteToWrite)
            }

        }
        if (!ConfigManager.bufferInit()) {
            MsgPrinter.logE("初始化buffer失败，请确认是否已初始化！")
            return
        }
        if (ConfigManager.toWriteBf?.remaining() ?: 0 < byteToWrite.size) {
            //空间不足，扩容或者切换文件
            if (ConfigManager.curFileCantIncrease(byteToWrite.size)) {
                //切换文件
                ConfigManager.clearBuffer()
                initBuffers(switchFile(fileToWriteIn), byteToWrite)
            } else {//扩容
                val raf = createRaf(fileToWriteIn)
                increaseSpace(raf?.channel, ConfigManager.curFileSavedSize(), byteToWrite.size)
                closeRaf(raf)
            }
            if (!ConfigManager.bufferInit()) {
                MsgPrinter.logE("切换文件并初始化buffer失败，请确认是否已初始化！")
                return
            }
        }
        val preSize = ConfigManager.curFileSavedSize()
        val afterSize = (preSize + byteToWrite.size)
        try {
            ConfigManager.sizeBf?.putLong(afterSize)
            ConfigManager.toWriteBf?.put(byteToWrite)
        } catch (e: Throwable) {
            try {
                ConfigManager.sizeBf?.clear()
                ConfigManager.sizeBf?.putLong(preSize)
            } catch (eI: Throwable) {
                eI.printStackTrace()
            }
            e.printStackTrace()
        }
    }

    private fun closeRaf(raf: RandomAccessFile?) {
        raf?.channel?.close()
        raf?.close()
    }

    /**
     * 旧文件已写满，需开辟新文件
     */
    private fun switchFile(oldFile: File?): File? {
        val oldName = oldFile?.name
        val oldIndex = ConfigManager.indexFromFileName(oldName)
        MsgPrinter.logD("切换文件执行，oldFile = ${oldFile?.name}")
        val createNewFile = ConfigManager.createNewFileWithIndex(oldIndex)
        //删除多余文件
        ConfigManager.ensureTotalFileSize()
        return createNewFile
    }

    //io 操作
    /**
     * @return -1:单个文件size超限，0：成功，-2初始化失败
     */
    private fun initBuffers(fileToWriteIn: File?, byteToWrite: ByteArray): Int {
        if (fileToWriteIn == null || fileToWriteIn.parentFile == null) {
            return -2
        }
        if (!fileToWriteIn.exists()) {
            if (!fileToWriteIn.parentFile!!.exists()) {
                fileToWriteIn.parentFile!!.mkdirs()
            }
            fileToWriteIn.createNewFile()
        }
        val raf = createRaf(fileToWriteIn) ?: return -2
        val sizeBits = Long.SIZE_BYTES.toLong()
        val channel = raf.channel
        ConfigManager.sizeBf = channel.map(FileChannel.MapMode.READ_WRITE, 0, sizeBits)//起始64位存放有效长度

        val savedSize = ConfigManager.curFileSavedSize()
        if (ConfigManager.isOverSize(savedSize, byteToWrite.size)) {
            ConfigManager.sizeBf = null
            return -1
        }
        val beginPosition = savedSize + sizeBits //有效内容起始位置
        //每次开辟100KB + 本次content的长度，如果content过长，会导致单个文件超过1MB
        increaseSpace(channel, savedSize, byteToWrite.size)
        MsgPrinter.logD("Buffer初始化成功，savedSize = $savedSize ,beginPosition = $beginPosition")

        closeRaf(raf)
        return if (ConfigManager.bufferInit()) 0 else -2
    }

    private fun createRaf(fileToWriteIn: File?): RandomAccessFile? {
        try {
            return RandomAccessFile(fileToWriteIn, "rw")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 扩容,创建map对象
     */
    private fun increaseSpace(channel: FileChannel?, savedSize: Long, contentSize: Int): Boolean {
        if (!ConfigManager.isMobileEnoughSpace()) {
            MsgPrinter.logE("扩容失败！当前手机空间不足10MB")
            ConfigManager.toWriteBf = null
            return false
        }
        ConfigManager.toWriteBf = channel?.map(
            FileChannel.MapMode.READ_WRITE,
            savedSize + Long.SIZE_BYTES.toLong(),
            ConfigManager.increaseSpace() + contentSize
        )
        return true
    }
}