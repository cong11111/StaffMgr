package com.loan.staffmgr.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.CollectionLogResponse
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.ui.collectionlog.CollectionLogAdapter
import com.loan.staffmgr.utils.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class CollectionLogActivity : BaseActivity() {

    companion object {
       const val TAG = "CollectionLogActivity"

        var mOrderId : Long? = null

        fun showMe(context : Context, orderId : Long?) {
            mOrderId = orderId
            val intent = Intent(context, CollectionLogActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var rvContent : RecyclerView? = null
    private var ivBack : AppCompatImageView? = null
    private var mAdapter : CollectionLogAdapter? = null

    private var mList = ArrayList<CollectionLogResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.theme_color))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_collection_log)
        initView()
        requestCollectionLog()
    }

    private fun initView() {
        rvContent = findViewById<RecyclerView>(R.id.rv_connection_log)
        ivBack = findViewById<AppCompatImageView>(R.id.iv_collection_log_back)

        rvContent?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = CollectionLogAdapter(mList)
        rvContent?.adapter = mAdapter
        rvContent?.addItemDecoration(object : RecyclerView.ItemDecoration() {

        })
        ivBack?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })
    }

   private fun requestCollectionLog() {
       val jsonObject = JSONObject()
       jsonObject.put("order_id", mOrderId)

        OkGo.post<String>(Api.TICKET_LOG_LIST).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val responseStr = CheckResponseUtils.checkResponseSuccess(response)
                    if (TextUtils.isEmpty(responseStr)) {
                        handleError()
                        return
                    }
                    val collectionLogs = com.alibaba.fastjson.JSONObject.parseArray(responseStr, CollectionLogResponse::class.java)
                    if (collectionLogs == null || collectionLogs.isEmpty()) {
                        handleError()
                        return
                    }
                    mList.clear()
                    mList.addAll(collectionLogs)
                    mAdapter?.notifyDataSetChanged()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    handleError()
                    if (BuildConfig.DEBUG) {
                        Log.e(RecordActivity.TAG, "request collection log failure = " + response.body())
                    }
                }
            })
    }

    private fun handleError() {

    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}