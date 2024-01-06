package com.loan.staffmgr.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
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
        val KEY_ACCOUNT = "key_sign_in_account"
        val KEY_PASS_CODE = "key_sign_in_pass_code"
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
        login(account!!, pwd!!, authCode!!)
    }

    private fun requestVerifyCode(mobile : String) {
        val jsonObject = JSONObject()
//        "test1@icredit.com", "test2@icredit.com", "test3@icredit.com"
        jsonObject.put("mobile", "test1@icredit.com")
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

    private fun login(account : String, pwd : String, captcha : String){
        val jsonObject = JSONObject()
        jsonObject.put("account", account)
        jsonObject.put("password", pwd)
        jsonObject.put("captcha", captcha)
        OkGo.post<String>(Api.LOGIN).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    flLoading?.visibility = View.GONE

                    val loginResponse: LoginResponse? =
                        checkResponseSuccess(response, LoginResponse::class.java)
                    if (loginResponse == null) {
                        Log.e(TAG, " request login error ." + response.body())
                        return
                    }
                    toHomePage(loginResponse.token, account, pwd)
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

    private fun toHomePage(token : String? , account : String, password : String) {
        Constant.mToken = token
        SPUtils.getInstance().put(KEY_ACCOUNT, account)
        SPUtils.getInstance().put(KEY_PASS_CODE, password)
    }

}