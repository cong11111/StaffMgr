package com.loan.staffmgr.ui.setting

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.ZipUtils
import com.loan.icreditapp.ui.setting.SettingItemDecor
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseFragment
import com.loan.staffmgr.bean.SettingBean
import com.loan.staffmgr.global.Constant
import com.loan.staffmgr.ui.AboutActivity
import com.loan.staffmgr.ui.LoginActivity
import com.loan.staffmgr.ui.MainActivity
import com.loan.staffmgr.utils.BuildRequestJsonUtils
import com.loan.staffmgr.utils.log.LogSaver
import com.lzy.okgo.OkGo
import java.io.File

class SettingFragment : BaseFragment() {

    private val TAG = "SettingFragment"

    private var rvContent: RecyclerView? = null
    private var mAdater: SettingAdapter? = null

    private var mList: ArrayList<SettingBean> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_setting, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvContent = view.findViewById(R.id.rv_setting_content)
        buildSettingList()
        var manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvContent?.layoutManager = manager
        rvContent?.addItemDecoration(SettingItemDecor())

        mAdater = SettingAdapter(mList)
        mAdater?.setOnClickListener(object : SettingAdapter.OnClickListener {
            override fun OnClick(pos: Int, settingBean: SettingBean) {
                when (settingBean.type) {
//                    PageType.MY_LOAN, PageType.MY_PROFILE, PageType.CARD,
//                    PageType.BANK_ACCOUNT, PageType.MESSAGE, PageType.HELP,
//                    PageType.ABOUT , PageType.CONTACT_US , PageType.OFFLINE_REPAY-> {
//                        updateMainPage(settingBean.type)
//                        closeSlide()
//                    }
                    PageType.LOGOUT -> {
                        if (checkClickFast()) {
                            return
                        }
                        logOut()
                        closeSlide()
                    }
                    PageType.ABOUT -> {
                        startActivity(Intent(context, AboutActivity::class.java))
                        closeSlide()
                    }
//                    PageType.TEST_TO_PROFILE2 -> {
//                        test1()
//                        closeSlide()
//                    }
                    PageType.FEED_BACK ->{
                        if (checkClickFast()){
                            return
                        }
                        startFeedBackEmail()
                    }
                }
            }

        })
        rvContent?.adapter = mAdater
    }


    private var mEmail : String? = null
    private fun startFeedBackEmail() {
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<String?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): String? {
                var logFoldPath = File(LogSaver.getLogFileFolder())
                if (logFoldPath.listFiles().isNotEmpty()) {
                    val srcFile = logFoldPath.listFiles()[0]
                    val traceFile =
                        File(requireContext().filesDir.absolutePath + "/log/", "trace")
                    FileUtils.createFileByDeleteOldFile(traceFile)
                    val success = ZipUtils.zipFile(srcFile, traceFile)
                    if (success) {
                        return traceFile.absolutePath
                    }
                }
                return null
            }

            override fun onSuccess(result: String?) {
                startFeedBackEmail(result)
            }
        })

//        try {
//
//            if (TextUtils.isEmpty(mEmail)){
//                mEmail = "support@creditng.com"
//            }
//            val data = Intent(Intent.ACTION_SEND)
////            data.data = Uri.parse(mEmail)
//            data.setType("text/plain")
//            val addressEmail = arrayOf<String>(mEmail!!)
//            data.putExtra(Intent.EXTRA_EMAIL, addressEmail)
//
//            data.putExtra(Intent.EXTRA_SUBJECT, "Icredit Feedback")
//            val mobile = SPUtils.getInstance().getString(Constant.KEY_ACCOUNT)
//            data.putExtra(Intent.EXTRA_TEXT, "Hi:  num " + mobile + ", I want to feedback....")
//            activity?.startActivity(data)
//
//        } catch (e: Exception) {
//            startChoose()
//            if (BuildConfig.DEBUG) {
//                if ((e is ActivityNotFoundException)) {
//                    ToastUtils.showShort(" not exist email app")
//                }
//            }
//        }
    }

    private fun startChoose(){
        if (TextUtils.isEmpty(mEmail)){
            mEmail = "support@creditng.com"
        }
        val data = Intent(Intent.ACTION_SEND)
        data.data = Uri.parse(mEmail)
        data.setType("text/plain")
        val addressEmail = arrayOf<String>(mEmail!!)
        data.putExtra(Intent.EXTRA_EMAIL, addressEmail)

        data.putExtra(Intent.EXTRA_SUBJECT, "Icredit Feedback")
        val mobile = SPUtils.getInstance().getString(Constant.KEY_ACCOUNT)
        data.putExtra(Intent.EXTRA_TEXT, "Hi:  num " + mobile + ", I want to feedback....")
        activity?.startActivity(Intent.createChooser(data, "Icredit Feedback:"))
    }

    private fun buildSettingList() {
        mList.clear()


//        mList.add(
//            SettingBean(
//                R.drawable.ic_message,
//                R.string.setting_message,
//                PageType.MESSAGE,
//                true
//            )
//        )
        mList.add(SettingBean(R.drawable.ic_help, R.string.setting_feed_back, PageType.FEED_BACK, false))
        mList.add(SettingBean(R.drawable.ic_about, R.string.setting_about, PageType.ABOUT))
        mList.add(SettingBean(R.drawable.ic_out, R.string.setting_logout, PageType.LOGOUT))

//        if (BuildConfig.DEBUG && true) {
        if (true) {
//            mList.add(SettingBean(R.drawable.ic_about, R.string.setting_rate_us, PageType.RATE_US))
//            mList.add(
//                SettingBean(
//                    R.drawable.ic_out,
//                    R.string.setting_test3,
//                    PageType.TEST_TO_PROFILE3,
//                    (if (TextUtils.equals(Api.HOST, "http://srv.creditng.com")) " Release " else " Debug ") + Api.HOST
//                )
//            )
        }
    }

    private fun updateMainPage(@PageType type: Int) {
        if (activity is MainActivity) {
            var main: MainActivity = activity as MainActivity
//            main.updatePageByType(type)
        }
    }

    private fun closeSlide(){
        if (activity is MainActivity) {
            var main: MainActivity = activity as MainActivity
            main.closeSlide()
        }
    }

    private fun test(){
//        val intent = Intent(context, AddProfileActivity::class.java)
//        val intent = Intent(context, AddBankAccount1Fragment::class.java)
//        val intent = Intent(context, PayActivity2::class.java)
//        startActivity(intent)
//        PayActivity2.launchPayActivity(activity!!, "","")
//        BindNewCardActivity.launchAddBankAccount(context!!)
//        val test = Test()
//        test.test1()
        if (true) {
            return
        }
        var startTime = System.currentTimeMillis()
        if (BuildConfig.DEBUG) {
            Log.e("Test", " start upload --------------------------------")
        }

    }

    private fun test1(){
        var startTime = System.currentTimeMillis()

    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }

    private fun logOut() {
        ToastUtils.showShort("logout success")
        Constant.mToken = null
        Constant.mName = null

        SPUtils.getInstance().put(Constant.KEY_TOKEN, "")
        SPUtils.getInstance().put(Constant.KEY_EXPIRE, "")

        val header = BuildRequestJsonUtils.clearHeaderToken()
        OkGo.getInstance().addCommonHeaders(header)
        var intent: Intent = Intent(activity, LoginActivity::class.java)
        activity?.startActivity(intent)
        activity?.overridePendingTransition(R.anim.slide_in_left_my, R.anim.slide_out_right_my)
        activity?.finish()
    }

    fun selectNone(){
        mAdater?.setCurPos(-1)
    }

    private fun startFeedBackEmail(traceFile: String?) {
        try {
            val data = Intent(Intent.ACTION_SEND)
            val email = "wang867103701@gmail.com"
            data.data = Uri.parse(email)
            data.setType("text/plain")
            val addressEmail = arrayOf<String>(email!!)
            data.putExtra(Intent.EXTRA_EMAIL, addressEmail)
//            if (!Constant.IS_AAB_BUILD){
//                val addressCC = arrayOf<String>("wang867103701@gmail.com")
//                data.putExtra(Intent.EXTRA_CC, addressCC)
//            }
            data.putExtra(Intent.EXTRA_SUBJECT, "Icredit Feedback")
            val mobile = SPUtils.getInstance().getString(Constant.KEY_ACCOUNT)
            data.putExtra(Intent.EXTRA_TEXT, "Hi:  num " + mobile + ",")
            if (!TextUtils.isEmpty(traceFile)) {
                data.putExtra(
                    Intent.EXTRA_STREAM,
                    getFileUri(requireContext(), File(traceFile!!), getAuthority(requireContext()))
                )
            }
            activity?.startActivity(Intent.createChooser(data, "Icredit Feedback:"))
        } catch (e: Exception) {
            if ((e is ActivityNotFoundException)) {
                ToastUtils.showShort(" not exist email app")
            }
            if (BuildConfig.DEBUG) {
                throw e
            }
        }
    }

    private fun getFileUri(context: Context, file: File, authority: String): Uri? {
        var useProvider = false
        var canAdd = false
        if (file.exists()) {
            useProvider = true
            canAdd = true
        }
        return if (canAdd) {
            if (useProvider) {
                FileProvider.getUriForFile(getDPContext(context), authority, file)
            } else {
                Uri.fromFile(file)
            }
        } else null
    }

    private fun getDPContext(context: Context): Context {
        var storageContext: Context = context
        if (Build.VERSION.SDK_INT >= 24) {
            if (!context.isDeviceProtectedStorage) {
                val deviceContext = context.createDeviceProtectedStorageContext()
                storageContext = deviceContext
            }
        }
        return storageContext
    }

    private fun getAuthority(context: Context) =
        context.applicationInfo.packageName + ".fileprovider"
}
