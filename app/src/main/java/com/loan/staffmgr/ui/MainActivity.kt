package com.loan.staffmgr.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.DashboardResponse
import com.loan.staffmgr.bean.LoginResponse
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.global.Constant
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.Response
import org.json.JSONObject

class MainActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            val httpHeaders = HttpHeaders()
            httpHeaders.put("token", Constant.mToken)
            OkGo.getInstance().addCommonHeaders(httpHeaders)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestDashboard()
    }

    private fun requestDashboard() {
        val jsonObject = JSONObject()
        OkGo.post<String>(Api.DASH_BOARD).tag(LoginActivity.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val loginResponse: DashboardResponse? =
                        checkResponseSuccess(response, DashboardResponse::class.java)
                    if (loginResponse == null) {
                        Log.e(LoginActivity.TAG, " request dash board error ." + response.body())
                        return
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    if (isFinishing|| isDestroyed) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(LoginActivity.TAG, "request dash board failure = " + response.body())
                    }
                    ToastUtils.showShort("request dash board failure")
                }
            })

    }
}