package com.loan.staffmgr.utils.log
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.text.TextUtils
import com.example.myapplication.log.LogWriterConfig
import com.example.myapplication.log.MsgPrinter
import com.example.myapplication.log.logFileNameDiv
import com.example.myapplication.log.logPre
import com.example.myapplication.log.logTotalDir
import com.example.myapplication.log.mainProDir
import com.example.myapplication.log.minSpaceNeed
import com.example.myapplication.log.oneMSize
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.MappedByteBuffer
import java.util.*
import java.util.concurrent.*

internal class ConfigManager {
    companion object {
        @Volatile var isDebug = false
        private var config: LogWriterConfig? = null
        private var available = false
        private val threadExecutor: ExecutorService = ThreadPoolExecutor(
            1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(200000),
            ThreadPoolExecutor.DiscardPolicy()
        )
        private var stat : StatFs? = null


        //存放文件有效长度，64bit
        var sizeBf: MappedByteBuffer? = null

        //当前正在承担写入功能的区域
        var toWriteBf: MappedByteBuffer? = null
        internal fun init(context: Context, maxSize: Int) {
            if (context.applicationContext == null) {
                MsgPrinter.logE("applicationContext为空，LogWriterConfig初始化失败！")
                return
            }
            val processName = getProcessName()
            val packageName = context.packageName
            MsgPrinter.logD("当前进程名$processName,package = $packageName")
            config = LogWriterConfig()
            val notMainProcess = processName != packageName
            if (notMainProcess) {
                //子进程
                config?.processDir = "${processName?.replace(packageName, "") ?: "err"}/"
            }

            var tempMaxSize = maxSize
            if (tempMaxSize !in 2..10){
                //子进程默认为2，主进程默认为4
                tempMaxSize = if (notMainProcess) 2 else 4
                if (maxSize != -1){
                    MsgPrinter.logE("maxSize:${maxSize}非法，允许范围2~10，重新设置为：$tempMaxSize")
                }
            }
            config?.appContext = context.applicationContext
            val absolutePath = getDPContext(config?.appContext)?.filesDir?.absolutePath
            if (TextUtils.isEmpty(absolutePath)) {
                MsgPrinter.logD("日志路径为空！LogWriterConfig初始化失败！")
            } else {
                config?.maxLogSize = tempMaxSize
                MsgPrinter.logD("进程：${config?.processDir}日志可用空间为:${config?.maxLogSize}")
                config?.totalFileFolder = "${absolutePath}$logTotalDir"
                config?.logFileFolder = "${config?.totalFileFolder}${config?.processDir ?: ""}"
                setValidLogFile(findCurValidFile(config?.logFileFolder))
                deleteOverSizeFiles(config?.maxLogSize)
                available = true
            }
        }

        private fun setValidLogFile(file: File?) {
            config?.curValidLogFile = file
        }

        fun totalFileFolder(): String {
            return config?.totalFileFolder ?: ""
        }

        /**
         * 当一个日志文件写满之后，需要创建新文件
         */
        fun createNewFileWithIndex(oldIndex: Int = 0): File? {
            val createNewLogFile = createNewLogFile(config?.logFileFolder, oldIndex)
            setValidLogFile(createNewLogFile)
            return createNewLogFile
        }

        fun bufferInit(): Boolean {
            return toWriteBf != null && sizeBf != null
        }

        fun clearBuffer() {
            sizeBf = null
            toWriteBf = null
        }

        private fun getProcessName(): String? {
            return try {
                val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
                val mBufferedReader = BufferedReader(FileReader(file))
                val processName = mBufferedReader.readLine().trim { it <= ' ' }
                mBufferedReader.close()
                processName
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getCalendar():Calendar?{
            return config?.calendar
        }

        /**
         * 确保总文件大小不会超过限制
         */
        fun ensureTotalFileSize() {
            deleteOverSizeFiles(config?.maxLogSize)
        }

        /**
         * 单个文件最大size
         */
        fun oneFileMaxSize():Int{
            return config?.maxSizePerFile?: oneMSize
        }
        /**
         * 当前文件是否不可以继续扩容
         * @return ture:需要切换文件了
         */
        fun curFileCantIncrease(newSize: Int): Boolean {
            return isOverSize(curFileSavedSize(), newSize)
        }

        fun isOverSize(curSize: Long, newSize: Int): Boolean {
            return curSize + newSize > oneFileMaxSize()
        }

        fun increaseSpace(): Long {
            return config?.increaseSpace ?: 0
        }

        fun curFileSavedSize(): Long {
            //todo 确认是否可以优化
            flipBufferSize(sizeBf)
            val result = sizeBf?.long ?: 0
            flipBufferSize(sizeBf)
            return result
        }

        private fun flipBufferSize(bufferSize: MappedByteBuffer?) {
            bufferSize?.flip()
            bufferSize?.clear()
        }

        /**
         * 是否初始化成功
         */
        fun available(): Boolean {
            return available
        }

        /**
         * 日志文件的所属文件夹
         */
        fun getLogFileRootPath(): String {
            val logFileFolder = config?.logFileFolder
            return if (TextUtils.isEmpty(logFileFolder)) "" else logFileFolder!!
        }

        fun executor(runnable: Runnable) {
            threadExecutor.execute(runnable)
        }

        fun getCurValidFile(): File? {
            return config?.curValidLogFile
        }

        /**
         * 是否是主进程
         */
        fun isMainProcess(): Boolean {
            return config?.processDir == mainProDir
        }
        /*fun deleteAll(){
            val totalFolder = config?.totalFileFolder ?: return
            try {
                clearBuffer()
                deleteAllFile(File(totalFolder))
            } catch (e: Exception) {
                MsgPrinter.logE("删除所有文件失败！请查看以下异常信息")
                e.printStackTrace()
            }finally {
                setValidLogFile(findCurValidFile(config?.logFileFolder))
            }
        }
        private fun deleteAllFile(file: File) {
            if (file.isDirectory) {
                //只删除文件，文件夹保留
                val files = file.listFiles()
                files?.forEach {
                    deleteAllFile(it)
                }
            } else if (file.exists()) {
                file.delete()
            }
        }*/
        /**
         * 去除文件名中间的数字
         */
        fun indexFromFileName(fileName: String?): Int {
            return if (TextUtils.isEmpty(fileName) || !fileName!!.contains(logFileNameDiv)) 0 else {
                val split = fileName.split(logFileNameDiv)
                if (split.size != 3) 0 else split[1].toInt()
            }
        }

        /**
         * 如果日志文件总大小超过限制，则删除多余的文件
         */
        private fun deleteOverSizeFiles(maxLogFileCount: Int?) {
            val maxCount = maxLogFileCount ?: 5 //默认5个
            val logFileRootPath = getLogFileRootPath()
            val rootFile = File(logFileRootPath)
            if (!rootFile.exists() || !rootFile.isDirectory) {
                return
            }
            val listFiles = rootFile.listFiles()
            if (listFiles == null || listFiles.size <= maxCount) {
                return
            }
            //旧文件排在前面，接下来删除多余文件
            listFiles.sortBy {
                indexFromFileName(it.name)
            }
            val totalFileCount = listFiles.size
            val countToDel = totalFileCount - maxCount
            MsgPrinter.logD("将要删除多余的日志文件，个数=${countToDel}")
            for (index in 0 until countToDel) {
                val file = listFiles[index]
                MsgPrinter.logD("删除文件：${file.name}")
                file.delete()
            }
            MsgPrinter.logD("删除完成，剩余日志文件个数：${rootFile.listFiles()?.size}")
        }

        /**
         * 从已有日志文件中查找当前生效的日志文件。
         * 如没有日志文件则新创建一个。
         */
        private fun findCurValidFile(folder: String?): File? {
            if (TextUtils.isEmpty(folder)) {
                return null
            }
            val file = File(folder!!)
            if (!file.exists()) {
                file.mkdirs()
            }
            val listFiles = file.listFiles()
            if (listFiles == null || listFiles.isEmpty()) {
                //创建新文件
                val desFile = createNewFileWithIndex()
                MsgPrinter.logD("创建初始文件：${desFile?.absolutePath}")
                return desFile
            }
            var tempDesFile = listFiles[0]
            listFiles.forEach {
                MsgPrinter.logD("${it.name}的最近修改时间:${it.lastModified()}")
                if (!it.name.contains(".") && indexFromFileName(tempDesFile.name) < indexFromFileName(it.name)) {
                    tempDesFile = it
                }
            }
            MsgPrinter.logD("ValidFile最终确定为${tempDesFile.name}")
            return tempDesFile
        }

        /**
         * 文件名：log_递增数字_时间戳
         * oldIndex,旧文件数字
         */
        private fun createNewLogFile(folder: String?, oldIndex: Int): File? {
            if (folder == null) {
                return null
            }
            if (!isMobileEnoughSpace()){
                MsgPrinter.logE("文件创建失败！当前手机剩余空间不足10MB")
                return null
            }
            try {
                val folderF = File(folder)
                if (!folderF.exists()) {
                    folderF.mkdirs()
                }
                val desFile = File("${folder}$logPre$logFileNameDiv${oldIndex + 1}$logFileNameDiv${System.currentTimeMillis()}")
                desFile.createNewFile()
                return desFile
            } catch (e: Exception) {
                MsgPrinter.logE("文件创建失败！oldIndex = $oldIndex")
                e.printStackTrace()
            }
            return null
        }

        fun isMobileEnoughSpace() = getFreeInternalStorage() > minSpaceNeed

        private fun getFreeInternalStorage(): Long {
            return try {
                val path: File = Environment.getDataDirectory()
                if (stat == null){
                    stat = StatFs(path.path)
                }
                stat!!.restat(path.path)
                stat!!.availableBytes
            } catch (e: Exception) {
                e.printStackTrace()
                0
            } catch (e: Error) {
                e.printStackTrace()
                0
            }
        }

        private fun getDPContext(context: Context?): Context? {

            var storageContext: Context? = context ?: return null
            if (Build.VERSION.SDK_INT >= 24 && !context.isDeviceProtectedStorage) {
                val deviceContext = context.createDeviceProtectedStorageContext()
                storageContext = deviceContext
            }
            return storageContext
        }
    }
}