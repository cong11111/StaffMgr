package com.loan.staffmgr.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.SimpleCallback
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.BaseResponseBean
import com.loan.staffmgr.bean.CaptchaResponse
import com.loan.staffmgr.bean.LoginResponse
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.global.Constant
import com.loan.staffmgr.utils.CodeUtils
import com.loan.staffmgr.widget.EditTextContainer
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    companion object {

        const val TAG = "LoginActivity"
    }

    private var ivVerify : AppCompatImageView? = null
    private var flLoading : View? = null
    private var etPhoneNum : EditTextContainer? = null
    private var etPhonePwd : EditTextContainer? = null
    private var etVerify : EditTextContainer? = null
    private var flCommit : FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ivVerify = findViewById<AppCompatImageView>(R.id.iv_signin_verify)
        flLoading = findViewById<View>(R.id.fl_siginin_loading)
        etPhoneNum = findViewById<EditTextContainer>(R.id.et_signin_phone_num)
        etPhonePwd = findViewById<EditTextContainer>(R.id.et_signin_pwd)
        etVerify = findViewById<EditTextContainer>(R.id.et_signin_verify)
        flCommit = findViewById<FrameLayout>(R.id.fl_signin_commit)

        etPhoneNum?.getEditText()?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val str = etPhoneNum?.getText()
                if (!TextUtils.isEmpty(str)){
                    requestVerifyCode(str!!)
                }
            }
        }

        flCommit?.setOnClickListener {
            checkAndLogin()
        }

        etPhonePwd?.setPassWordMode(true)
        val account = SPUtils.getInstance().getString(Constant.KEY_ACCOUNT)
        val pwd = SPUtils.getInstance().getString(Constant.KEY_PASS_CODE)
        if (!TextUtils.isEmpty(account)) {
            etPhoneNum?.getEditText()?.setText(account)
            etPhoneNum?.setSelectionLast()
            requestVerifyCode(account)
        }
        if (!TextUtils.isEmpty(pwd)) {
            etPhonePwd?.getEditText()?.setText(pwd)
            etPhonePwd?.setSelectionLast()
        }
        if (BuildConfig.DEBUG && TextUtils.isEmpty(account) && TextUtils.isEmpty(pwd)) {
            etPhoneNum?.getEditText()?.setText("Collection2@icredit.com")
            etPhonePwd?.getEditText()?.setText("Ab!123457")
            requestVerifyCode(account)
        }
    }

    private fun checkAndLogin() {
        val account = etPhoneNum?.getText()?.replace(" ", "")
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showShort("need input account .")
            return
        }
        val pwd = etPhonePwd?.getText()?.replace(" ", "")
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showShort("need input password .")
            return
        }

        var authCode : String? = etVerify?.getText()?.replace(" ", "")
        if (TextUtils.isEmpty(authCode)) {
            ToastUtils.showShort("need input verify .")
            return
        }
        if (checkClickFast()){
            return
        }
        PermissionUtils.permission(Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_SMS).callback(object : SimpleCallback {
            override fun onGranted() {
                login(account!!, pwd!!, authCode!!)
            }

            override fun onDenied() {

            }

        }).request()
    }

    private fun requestVerifyCode(mobile : String) {
        val jsonObject = JSONObject()
//        "test1@icredit.com", "test2@icredit.com", "test3@icredit.com"
        // hello123
        jsonObject.put("mobile", getPhoneNum())
        OkGo.getInstance().cancelTag(TAG)
        OkGo.post<String>(Api.SEND_SMS).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
//                    flLoading?.visibility = View.GONE
                    val captchaResponse: CaptchaResponse? =
                        checkResponseSuccess(response, CaptchaResponse::class.java)
                    if (captchaResponse == null) {
                        Log.e(TAG, " request verify error ." + response.body())
                        return
                    }
                    if (BuildConfig.DEBUG && !TextUtils.isEmpty(captchaResponse?.captcha)) {
                        etVerify?.getEditText()?.setText(captchaResponse?.captcha)
                        etVerify?.setSelectionLast()
                    }
                    ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<Bitmap?>() {
                        override fun doInBackground(): Bitmap? {
                            return CodeUtils.getInstance().createBitmap(captchaResponse.captcha)
                        }

                        override fun onSuccess(result: Bitmap?) {
                            if (result == null || result.isRecycled) {
                                return
                            }
                            ivVerify?.setImageBitmap(result)
                        }

                    })
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
//                    flLoading?.visibility = View.GONE
                    if (isFinishing|| isDestroyed) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "request verify failure = " + response.body())
                    }
                    ToastUtils.showShort("request verify  failure")
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun login(account : String, pwd : String, captcha : String){
        val jsonObject = JSONObject()
        var phoneNumber : String? = getPhoneNum()
//        Log.e("Test", " mobile =  $phoneNumber")
        jsonObject.put("account", account)
        jsonObject.put("password", pwd)
        jsonObject.put("mobile", phoneNumber)
        jsonObject.put("captcha", captcha)
        jsonObject.put("appVersionCode", AppUtils.getAppVersionCode())
        OkGo.post<String>(Api.LOGIN).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    flLoading?.visibility = View.GONE
                    var str = response.body().toString()
                    // TODO
                    str = str.replace("\n","")
                    val responseBean = com.alibaba.fastjson.JSONObject.parseObject(str, BaseResponseBean::class.java)
                    if (responseBean.code == 998) {
                        ToastUtils.showShort(responseBean.message)
                        return
                    }
                    val loginResponse: LoginResponse? =
                        checkResponseSuccess(response, LoginResponse::class.java)
                    if (loginResponse == null) {
                        Log.e(TAG, " request login error ." + response.body())
                        return
                    }
                    toHomePage(loginResponse, account, pwd)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    flLoading?.visibility = View.GONE
                    if (isFinishing|| isDestroyed) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "request login failure = " + response.body())
                    }
                    ToastUtils.showShort("request login failure")
                }
            })
    }

    private fun toHomePage(login : LoginResponse? , account : String, password : String) {
        Constant.mToken = login?.token
        SPUtils.getInstance().put(Constant.KEY_ACCOUNT, account)
        SPUtils.getInstance().put(Constant.KEY_PASS_CODE, password)
        SPUtils.getInstance().put(Constant.KEY_TOKEN, Constant.mToken)
        SPUtils.getInstance().put(Constant.KEY_EXPIRE,login?.expireAt)
        MainActivity.start(this)
    }

    @SuppressLint("MissingPermission")
    private fun getPhoneNum(): String? {
        // TODO
        if (BuildConfig.DEBUG) {
            return "2341111111111"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val manager = this.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
           return manager.getPhoneNumber(SubscriptionManager.DEFAULT_SUBSCRIPTION_ID)
        } else {
            val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return  telephonyManager.getLine1Number()
        }
    }
}