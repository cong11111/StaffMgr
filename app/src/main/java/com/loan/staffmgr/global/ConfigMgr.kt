package com.loan.staffmgr.global

import android.text.TextUtils
import android.util.Log
import android.util.Pair
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.ui.fragment.ticket.TicketFragment
import com.loan.staffmgr.utils.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.util.*

object ConfigMgr {
    val TAG = "ConfigMgr"
    val mCommunicationWay = HashMap<String, String>()
    val mOverdueReason = HashMap<String, String>()
    val mResult = HashMap<String, String>()

    fun getAllConfig() {
        requestConfig()
    }

    fun requestConfig() {
        val jsonObject = JSONObject()
        OkGo.get<String>(Api.GET_CONFIG).tag(TAG)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    Log.e("Test", " " + response.body().toString())
                    val jsonObject = JSONObject(response.body().toString())
                    jsonObject.optJSONObject("communicationWay")
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)

                }
            })
    }

    private interface CallBack {
        fun onGetData(list: ArrayList<Pair<String, String>>)
    }

}