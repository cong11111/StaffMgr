package com.loan.staffmgr.ui.record

import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.dialog.selectdata.SelectDataDialog
import com.loan.staffmgr.ui.RecordActivity
import com.loan.staffmgr.utils.BuildRecordUtils
import java.util.*

class WhatAppFragment : BaseSubmitFragment() {

    companion object {
        const val TAG = "WhatAppFragment"

    }

    private var llTime : View? = null
    private var tvTime : AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_record_whatapp, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        llTime = view.findViewById<View>(R.id.ll_time_container)
        tvTime = view.findViewById<AppCompatTextView>(R.id.tv_time_result)

        llTarget?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (activity is RecordActivity) {
                    //选择当前联系人
                    val contactList = (activity as RecordActivity).getContactList()
                    val resultList = BuildRecordUtils.buildWhatAppList(contactList)
                    showListDialog(resultList, object : SelectDataDialog.Observer {
                        override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                            mContact = contactList[pos]
                            tvTarget?.text = content?.first
                            clearSelectTime()
                            clearFeedBack()
                            clearPromiseTime()
                            updateUi()
                        }
                    })
                }
            }
        })
        llTime?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                showTimePicker(object : OnTimeSelectListener {
                    override fun onTimeSelect(date: Date?, v: View?) {
                        if (date == null) {
                            return
                        }
                        val phoneTimeStr = BuildRecordUtils.convertMillionToStr(date.time)
                        tvTime?.text = phoneTimeStr
                        // 打电话时间
                        mSaveLogRequest.phone_time = phoneTimeStr
                        clearFeedBack()
                        clearPromiseTime()
                        updateButtonState()
                    }
                })
            }
        })
        llPromiseTime?.isEnabled = false
        llFeedBack?.isEnabled = false
        llPromiseTime?.isSelected = false
        llFeedBack?.isSelected = false
        llTime?.isSelected = false
        llTime?.isEnabled = false
    }

    private fun updateUi() {
        llPromiseTime?.isEnabled = true
        llFeedBack?.isEnabled = true
        llPromiseTime?.isSelected = true
        llFeedBack?.isSelected = true
        llTime?.isSelected = true
        llTime?.isEnabled = true
    }

    fun bindData() {

    }

    private fun clearFeedBack(){
        mSaveLogRequest.result = null
        tvFeedBack?.text = "Click to select:"
    }

    private fun clearSelectTime(){
        mSaveLogRequest.phone_time = null
        tvTime?.text = "Click to select:"
    }

    private fun clearPromiseTime(){
        mSaveLogRequest.promise_repay_time = null
        llPromiseTime?.visibility = View.GONE
    }
}