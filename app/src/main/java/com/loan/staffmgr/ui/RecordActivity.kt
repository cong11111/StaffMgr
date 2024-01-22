package com.loan.staffmgr.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CallLog
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.DashboardResponse
import com.loan.staffmgr.bean.collect.CallLogRecord
import com.loan.staffmgr.collect.CollectRecordLogMgr
import com.loan.staffmgr.dialog.selectdata.SelectDataDialog
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.utils.BuildRecordUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class RecordActivity : BaseActivity() {

    private var tvFeedBack : AppCompatTextView? = null
    private var tvTarget : AppCompatTextView? = null
    private var llFeedBack : LinearLayout? = null
    private var llTarget : LinearLayout? = null
    private var tvTakeTime : TextView? = null
    private var tvResult : TextView? = null
    private var tvCallTime : TextView? = null

    companion object {
        const val TAG = "RecordActivity"
        fun showMe(context : Context) {
            val intent = Intent(context, RecordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        initView()
    }

    private fun initView() {
        tvTarget = findViewById<AppCompatTextView>(R.id.tv_record_target)
        tvFeedBack = findViewById<AppCompatTextView>(R.id.tv_record_feedback)
        llFeedBack = findViewById<LinearLayout>(R.id.ll_feedback_container)
        llTarget = findViewById<LinearLayout>(R.id.ll_target_container)
        tvTakeTime = findViewById<TextView>(R.id.tv_record_take_time)
        tvResult = findViewById<TextView>(R.id.tv_record_result)
        tvCallTime = findViewById<TextView>(R.id.tv_record_call_time)

        llTarget?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                val list = BuildRecordUtils.buildTargetListTest()
                showListDialog(list, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        val target = getRecordByTarget(content!!.first, content!!.second)
                        if (target != null) {
                            updateUiByCallLogRecord(target)
                        }
                    }

                })
            }

        })
        llFeedBack?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                val list = BuildRecordUtils.buildFeedbackList()
                showListDialog(list, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {

                    }

                })
            }

        })
    }

    private fun updateUiByCallLogRecord(callLogRecord: CallLogRecord) {
        var resultStr : String
        var duration : String = ""
        when(callLogRecord.type) {
            (CallLog.Calls.OUTGOING_TYPE) -> {
                if (callLogRecord.duration != null && callLogRecord.duration!! > 0) {
                    resultStr = resources.getString(R.string.take)
                    duration = callLogRecord.duration.toString()
                } else {
                    resultStr = resources.getString(R.string.does_not_exist)
                }
            }
            else -> {
                resultStr = resources.getString(R.string.does_not_exist)
            }
        }
        tvResult?.text = resultStr
        tvCallTime?.text = callLogRecord.date.toString()
        tvTakeTime?.text = duration
    }

    private fun getRecordByTarget(num : String, date : String) : CallLogRecord?{
        for (index in 0 until CollectRecordLogMgr.mCallRecordList.size) {
            val callLogRecord = CollectRecordLogMgr.mCallRecordList[index]
            if (TextUtils.equals(callLogRecord.num, num) &&
                TextUtils.equals(callLogRecord.date.toString(), date)) {
                return callLogRecord
            }
        }
        return null
    }



    private fun showListDialog(
        list: ArrayList<Pair<String, String>>,
        observer: SelectDataDialog.Observer
    ) {
        val dialog = SelectDataDialog(this)
        val tempList = ArrayList<Pair<String, String>>()
        tempList.addAll(list)
        dialog.setList(tempList, observer)
        dialog.show()
    }

    private fun showTimePicker(listener: OnTimeSelectListener?) {
//        if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
//            KeyboardUtils.hideSoftInput(requireActivity())
//        }
        //时间选择器
        val pvTime = TimePickerBuilder(this, listener).setSubmitText("ok")
            .setCancelText("cancel").build()
        // TODO
//        pvTime.setDate()
        pvTime.show()
    }

    private fun saveRecord(ticketId : Int, ) {
        val jsonObject = JSONObject()
        jsonObject.put( "ticket_id", ticketId)
        jsonObject.put( "result", "Willing to pay")
        jsonObject.put( "remark", "test")
        jsonObject.put( "communication_way", 2)
        jsonObject.put( "phone_time", "2024-01-22 12:22:12")
        jsonObject.put( "phone_objects", 1)
        jsonObject.put( "phone_connected", 1)
        jsonObject.put( "repay_inclination", 1)
        jsonObject.put( "promise_repay_time", "2024-01-22 20:00")
        jsonObject.put( "overdue_reason_item", 2)
        OkGo.post<String>(Api.SAVE_TICKET_LOG).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
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
                        Log.e(TAG, "request save record failure = " + response.body())
                    }
                    ToastUtils.showShort("request save record  failure")
                }
            })
    }
}