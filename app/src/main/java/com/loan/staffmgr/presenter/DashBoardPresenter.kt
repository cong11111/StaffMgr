package com.loan.staffmgr.presenter

import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.DashboardResponse
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.ui.LoginActivity
import com.loan.staffmgr.utils.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class DashBoardPresenter(var activity: BaseActivity?, var observer: Observer?) {

    fun requestDetail() {
        val jsonObject = JSONObject()
        OkGo.post<String>(Api.DASH_BOARD).tag(LoginActivity.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val response: DashboardResponse? =
                        CheckResponseUtils.checkResponseSuccess(response, DashboardResponse::class.java)
                    if (response == null) {
                        Log.e(LoginActivity.TAG, " request dash board error ." + response)
                        return
                    }
                    if (activity == null || activity!!.isDestroy()) {
                        return
                    }
                    observer?.onSuccess(response)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (activity == null || activity!!.isDestroy()) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(LoginActivity.TAG, "request dash board failure = " + response.body())
                    }
                    ToastUtils.showShort("request dash board failure")
                    observer?.onFailure("request dash board failure")
                }
            })
    }

    fun onDestroy() {
        activity = null
    }

    interface Observer {
        fun onSuccess(response: DashboardResponse?)

        fun onFailure(errorMsg : String?)
    }
}