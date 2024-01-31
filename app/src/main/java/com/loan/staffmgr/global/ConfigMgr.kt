package com.loan.staffmgr.global

import android.text.TextUtils
import android.util.Log
import android.util.Pair
import com.alibaba.fastjson.JSONArray
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.bean.BaseResponseBean
import com.loan.staffmgr.bean.DueResult
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.ui.fragment.ticket.TicketFragment
import com.loan.staffmgr.utils.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

object ConfigMgr {
    val TAG = "ConfigMgr"

    var mCommunicationWay : ArrayList<Pair<String, String>> = ArrayList()
    var mOverdueReason : ArrayList<Pair<String, String>> = ArrayList()
    var mPhoneConnect : ArrayList<Pair<String, String>> = ArrayList()
    var mPhoneObject : ArrayList<Pair<String, String>> = ArrayList()
    var mResult : ArrayList<DueResult> = ArrayList<DueResult>()

    fun requestConfig() {
       val dataStr =  SPUtils.getInstance().getString(TAG, "")
        if (!TextUtils.isEmpty(dataStr)) {
            buildData(dataStr)
        }
        if (BuildConfig.DEBUG) {
            if (mCommunicationWay.isNotEmpty() &&
                mOverdueReason.isNotEmpty() &&
                mPhoneConnect.isNotEmpty() &&
                mPhoneObject.isNotEmpty() &&
                mResult.isNotEmpty()) {
                return
            }
        }
        val jsonObject = JSONObject()
        OkGo.get<String>(Api.GET_CONFIG).tag(TAG)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val responseStr = CheckResponseUtils.checkResponseSuccess(response)
                    if (TextUtils.isEmpty(responseStr)) {
                        return
                    }
                    SPUtils.getInstance().put(TAG, responseStr)
                    buildData(responseStr!!)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)

                }
            })
    }

    fun buildData(dataStr : String) {
        val jsonObject = JSONObject(dataStr)
        val communicationWay = jsonObject.optJSONObject("communicationWay")
        val overdueReason = jsonObject.optJSONObject("overdueReason")
        val phoneConnect = jsonObject.optJSONObject("phoneConnect")
        val phoneObject = jsonObject.optJSONObject("phoneObject")
        val result = jsonObject.optJSONArray("result")
        if (communicationWay != null) {
            val list = getHashMap(communicationWay)
            if ( list.isNotEmpty()) {
                mCommunicationWay.clear()
                mCommunicationWay.addAll(list)
            }
        }
        if (overdueReason != null) {
            val list = getHashMap(overdueReason)
            if (list.isNotEmpty()) {
                mOverdueReason.clear()
                mOverdueReason.addAll(list)
            }
        }
        if (phoneConnect != null) {
            val list = getHashMap(phoneConnect)
            if (list.isNotEmpty()) {
                mPhoneConnect.clear()
                mPhoneConnect.addAll(list)
            }
        }
        if (phoneObject != null) {
            val list = getHashMap(phoneObject)
            if (list.isNotEmpty()) {
                mPhoneObject.clear()
                mPhoneObject.addAll(list)
            }
        }
        if (result != null) {
            val resultList = JSONArray.parseArray(result.toString(), DueResult::class.java)
            if (resultList != null) {
                mResult.clear()
                mResult.addAll(resultList)
            }
        }
    }

    private interface CallBack {
        fun onGetData(list: ArrayList<Pair<String, String>>)
    }

    fun getHashMap(jsonObject: JSONObject?) : ArrayList<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        if (jsonObject == null) {
            return list
        }
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.optString(key)
            list.add(Pair(value, key))
        }
        return list
    }
    /**
     * {"result":[{"Status":1,"Ctime":1553569315540,"Utime":1553569329441,"Sort":1,"Pid":8,"Lang":"Willing to pay","Id":9,"SetId":1,"Name":"承诺还款"},{"Status":1,"Ctime":1553569356089,"Utime":1553569371652,"Sort":2,"Pid":8,"Lang":"Not Willing to pay","Id":10,"SetId":2,"Name":"无还款意愿"},{"Status":1,"Ctime":1553569396180,"Utime":1553569409953,"Sort":3,"Pid":8,"Lang":"Not Customer","Id":11,"SetId":3,"Name":"非借款人"},{"Status":1,"Ctime":1553569447410,"Utime":1553569466453,"Sort":4,"Pid":8,"Lang":"Continue Ringing","Id":12,"SetId":4,"Name":"振铃不接"},{"Status":1,"Ctime":1553569489731,"Utime":1553569503437,"Sort":5,"Pid":8,"Lang":"Out Of Coverage Area","Id":13,"SetId":5,"Name":"不在服务区"},{"Status":1,"Ctime":1553569526807,"Utime":1553569543709,"Sort":6,"Pid":8,"Lang":"Not Active","Id":14,"SetId":6,"Name":"暂时无法接通"},{"Status":1,"Ctime":1553569568763,"Utime":1553569568763,"Sort":7,"Pid":8,"Lang":"Call forwarding","Id":15,"SetId":7,"Name":"呼叫转接"},{"Status":1,"Ctime":1553569597049,"Utime":1553569597049,"Sort":8,"Pid":8,"Lang":"Busy","Id":16,"SetId":8,"Name":"用户忙"},{"Status":1,"Ctime":1553569635765,"Utime":1553569635765,"Sort":9,"Pid":8,"Lang":"Blocked all coming call","Id":17,"SetId":9,"Name":"被拉黑"},{"Status":1,"Ctime":1553569659214,"Utime":1553569659214,"Sort":10,"Pid":8,"Lang":"Not Registered (Invalid)","Id":18,"SetId":10,"Name":"空号"},{"Status":1,"Ctime":1553569682950,"Utime":1553569682950,"Sort":11,"Pid":8,"Lang":"Not receive the money","Id":19,"SetId":11,"Name":"未收到放款"},{"Status":1,"Ctime":1553569707384,"Utime":1553569707384,"Sort":12,"Pid":8,"Lang":"Already pay but delay","Id":20,"SetId":12,"Name":"已付款未入系统"},{"Status":1,"Ctime":1553569737908,"Utime":1553569737908,"Sort":13,"Pid":8,"Lang":"Left message to adressbook","Id":21,"SetId":13,"Name":"已留言联系人或通讯录"},{"Status":1,"Ctime":1553569769751,"Utime":1553569769751,"Sort":14,"Pid":8,"Lang":"Partial payment first","Id":22,"SetId":15,"Name":"意愿部分还款"},{"Status":1,"Ctime":1553569799787,"Utime":1553569799787,"Sort":15,"Pid":8,"Lang":"Nobody answer","Id":23,"SetId":16,"Name":"没声音"},{"Status":1,"Ctime":1557305236513,"Utime":1557305236513,"Sort":16,"Pid":8,"Lang":"PV abnormal","Id":26,"SetId":17,"Name":"电核异常"},{"Status":1,"Ctime":1557305236513,"Utime":1557305236513,"Sort":99,"Pid":8,"Lang":"SMS Urge","Id":36,"SetId":99,"Name":"短信催收"}],
     * "overdueReason":{"1":"No Money","100":"Others","2":"Children need to go school","3":"No salary","4":"Go Hospital(Ill, Accident on families)","5":"Forget the loan","6":" Don't know how to repay / try to repay in our APP but failed"},
     * "phoneObject":{"1":"Self","2":"Contact1","3":"Contact2","4":"Address Book","5":"联系人3","6":"联系人4"},
     * "communicationWay":{"1":"Whatsapp","2":"Phone","3":"SMS"},
     * "phoneConnect":{"0":"Unconnected","1":"Connected","2":"SMS Sent"}}
     */
}