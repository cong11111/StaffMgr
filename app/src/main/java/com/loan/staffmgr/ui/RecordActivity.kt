package com.loan.staffmgr.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.blankj.utilcode.util.KeyboardUtils
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.collect.CallLogRecord
import com.loan.staffmgr.collect.CollectRecordLogMgr
import com.loan.staffmgr.dialog.selectdata.SelectDataDialog

class RecordActivity : BaseActivity() {

    private var tvFeedBack : AppCompatTextView? = null
    private var tvTarget : AppCompatTextView? = null
    private var llFeedBack : LinearLayout? = null
    private var llTarget : LinearLayout? = null

    companion object {
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

        llTarget?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                val list = buildTargetList()
                showListDialog(list, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        val target = getRecordByTarget(content!!.first, content!!.second)
//                        target.
                    }

                })
            }

        })
        llFeedBack?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                val list = buildFeedbackList()
                showListDialog(list, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {

                    }

                })
            }

        })
    }

    @VisibleForTesting
    private fun buildTargetList() : ArrayList<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()

        val callRecordList = java.util.ArrayList<CallLogRecord>()
        callRecordList.addAll( CollectRecordLogMgr.mCallRecordList)
        for (index in 0 until callRecordList.size) {
            val callRecord = callRecordList[index]
            if (!TextUtils.isEmpty(callRecord.num) && (
                        callRecord.num!!.contains("18939440244") ||
                        callRecord.num!!.contains("18539253050")

                    )) {
                if (!TextUtils.isEmpty(callRecord.num)) {
                    list.add(Pair(callRecord.num, callRecord.date.toString()))
                }
            }
        }
        return list
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

    private fun buildFeedbackList() : ArrayList<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        val str1 = resources.getString(R.string.willing_to_pay)
        val str2 = resources.getString(R.string.no_willing)
        val str3 = resources.getString(R.string.not_customer)
        val str4 = resources.getString(R.string.not_receive_money)
        val str5 = resources.getString(R.string.already_paid_but_delay)
        val str6 = resources.getString(R.string.partial_pay_first)
        val str7 = resources.getString(R.string.not_pick)

        list.add(Pair(str1, str1))
        list.add(Pair(str2, str2))
        list.add(Pair(str3, str3))
        list.add(Pair(str4, str4))
        list.add(Pair(str5, str5))
        list.add(Pair(str6, str6))
        list.add(Pair(str7, str7))
        return list
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
}