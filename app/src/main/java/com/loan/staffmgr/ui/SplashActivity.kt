package com.loan.staffmgr.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.DashboardResponse
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.global.Constant
import com.loan.staffmgr.presenter.DashBoardPresenter
import com.loan.staffmgr.utils.BuildRequestJsonUtils
import com.loan.staffmgr.utils.log.LogSaver
import com.lzy.okgo.OkGo
import org.json.JSONException
import org.json.JSONObject

class SplashActivity : BaseActivity() {

    private val TAG = "LauncherActivity"

    private val TO_WELCOME_PAGE = 111

    private val TO_MAIN_PAGE = 112

    private var requestTime: Long = 0

    private var mHandler: Handler? = null
    private var view: View? = null
    private var presenter: DashBoardPresenter? = null

    init {
        mHandler = Handler(
            Looper.getMainLooper()
        ) { message ->
            when (message.what) {
                TO_WELCOME_PAGE -> {
                    mHandler?.removeCallbacksAndMessages(null)
                    OkGo.getInstance().cancelTag(TAG)
                    val login2Intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(login2Intent)
                    finish()
                }
                TO_MAIN_PAGE -> {
                    mHandler?.removeCallbacksAndMessages(null)
                    OkGo.getInstance().cancelTag(TAG)
                    val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            }
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.theme_color))
        BarUtils.setStatusBarLightMode(this, false)
        setContentView(R.layout.activity_splash)
        view = findViewById<View>(R.id.container)

        val account = SPUtils.getInstance().getString(Constant.KEY_ACCOUNT)
        val pwd = SPUtils.getInstance().getString(Constant.KEY_PASS_CODE)
        val token = SPUtils.getInstance().getString(Constant.KEY_TOKEN)
        val expire = SPUtils.getInstance().getString(Constant.KEY_EXPIRE)
//        val lastLoginTime = SPUtils.getInstance().getLong(Constant.KEY_LOGIN_TIME, 0L)
//        var canUseToken = true
//        if (lastLoginTime > 0 && System.currentTimeMillis() >= lastLoginTime) {
//            val deltaTime = System.currentTimeMillis() - lastLoginTime
//            val TIME : Long = 30L * 24 * 60 * 60 * 1000
//            if (deltaTime >= TIME) {
//                canUseToken = false
//            }
//        }
//        Constant.mToken = token
//        Log.e(TAG, " token = " + token)

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)
            || TextUtils.isEmpty(expire) || TextUtils.isEmpty(token) ||true) {
//            if (!canUseToken && BuildConfig.DEBUG) {
            if (BuildConfig.DEBUG) {
                mHandler?.postDelayed(Runnable {
                    if (isDestroy()) {
                        return@Runnable
                    }
                    ToastUtils.showShort("token desire")
                }, 300)
            }
//            LogSaver.logToFile("auto login token has desire last login time = $lastLoginTime" + " curTime = " + System.currentTimeMillis())
            mHandler?.sendEmptyMessageDelayed(TO_WELCOME_PAGE, 1000)
        } else {
            Constant.mToken = token
            val httpHeaders = BuildRequestJsonUtils.buildHeaderToken()
            OkGo.getInstance().addCommonHeaders(httpHeaders)
            presenter = DashBoardPresenter(this@SplashActivity, object :  DashBoardPresenter.Observer {
                override fun onSuccess(response: DashboardResponse?) {
                    if (isDestroy()) {
                        return
                    }
                    var successEnter = false
                    if (response != null) {
                        successEnter = true
                    }
                    mHandler?.sendEmptyMessageDelayed(if (successEnter) TO_MAIN_PAGE else TO_WELCOME_PAGE,100)
                }

                override fun onFailure(errorMsg: String?) {
                    mHandler?.sendEmptyMessage(TO_WELCOME_PAGE)
                }

            })
            presenter?.requestDetail()
            mHandler?.sendEmptyMessageDelayed(TO_WELCOME_PAGE, 3000)
        }
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        mHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}