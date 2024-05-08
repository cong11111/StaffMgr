package com.loan.staffmgr.utils

import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.bean.BaseResponseBean
import com.loan.staffmgr.event.LogTimeOut
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus

class CheckResponseUtils {

//    public static final Gson gson = new GsonBuilder()
//////             # 将DEFAULT改为STRING
//            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
//            .create();

    //    public static final Gson gson = new GsonBuilder()
    //////             # 将DEFAULT改为STRING
    //            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
    //            .create();

    companion object {
        fun <T> checkResponseSuccess(response: Response<String>, clazz: Class<T>?): T? {
            val body = checkResponseSuccess(response)
            return if (TextUtils.isEmpty(body)) {
                null
            } else JSONObject.parseObject(body, clazz)
        }

        fun checkResponse(response: Response<String>): BaseResponseBean? {
            var responseBean: BaseResponseBean? = null
            try {
                var str = response.body().toString()
                str = str.replace("\n","")
                responseBean = JSONObject.parseObject(
                    str,
                    BaseResponseBean::class.java
                )
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    throw e
                }
            }
            return responseBean
        }

        fun checkResponseSuccess(response: Response<String>): String? {
            var responseBean: BaseResponseBean? = null
            try {
                var str = response.body().toString()
//                Log.e("Test", " str = " + str)
                // TODO
                str = str.replace("\n","")
                responseBean = JSONObject.parseObject(
                    str,
                    BaseResponseBean::class.java
                )
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    throw e
                }
            }
            //        BaseResponseBean responseBean = gson.fromJson(response.body().toString(), BaseResponseBean.class);
            if (responseBean == null) {
                ToastUtils.showShort("request failure.")
                return null
            }
            if (responseBean.isLogout()){
                EventBus.getDefault().post(LogTimeOut())
                return null
            }
            if (!responseBean.isRequestSuccess()) {
                ToastUtils.showShort(responseBean.getMessage())
                return null
            }
            if (responseBean.getData() == null) {
                return null
            }
            return JSONObject.toJSONString(responseBean.getData())
        }
    }

}