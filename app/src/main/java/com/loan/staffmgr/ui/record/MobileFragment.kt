package com.loan.staffmgr.ui.record

import android.os.Bundle
import android.provider.CallLog
import android.text.TextUtils
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.bean.collect.CallLogRecord
import com.loan.staffmgr.dialog.selectdata.SelectDataDialog
import com.loan.staffmgr.ui.RecordActivity
import com.loan.staffmgr.utils.BuildRecordUtils

class MobileFragment : BaseSubmitFragment() {

    private var tvTakeTime : TextView? = null
    private var llCallTime : View? = null
    private var tvResult : TextView? = null
    private var tvCallTime : TextView? = null

    private var flNodata : View? = null
    private var llData : View? = null

    private val mCurCallTimeList = ArrayList<CallLogRecord>()

    companion object {
        const val TAG = "MobileFragment"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_record_mobile, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTakeTime = view.findViewById<TextView>(R.id.tv_record_take_time)
        tvResult = view.findViewById<TextView>(R.id.tv_record_result)
        llCallTime = view.findViewById<View>(R.id.ll_call_time_container)
        tvCallTime = view.findViewById<TextView>(R.id.tv_record_call_time)

        flNodata = view.findViewById<View>(R.id.fl_record_nodata)
        llData = view.findViewById<View>(R.id.ll_record_data)

        llTarget?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (activity is RecordActivity) {
                    //选择当前联系人
                    val contactList = (activity as RecordActivity).getContactList()
                    val resultList = BuildRecordUtils.buildTargetList(contactList)
                    showListDialog(resultList, object : SelectDataDialog.Observer {
                        override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                            val list = BuildRecordUtils.getRecordByTargetList(content!!.first, content.second)
                            mContact = contactList[pos]
                            mCurCallTimeList.clear()
                            var target : CallLogRecord? = null
                            if (list != null && list.isNotEmpty()) {
                                target = list[0]
                                mCurCallTimeList.addAll(list)
                            }
                            updateUiByCallLogRecord(target)
                        }
                    })
                }
            }
        })
        llCallTime?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(mContact == null) {
                    ToastUtils.showShort("unselect target.")
                    return
                }
                //选择具体某个通话记录
                val resultList = BuildRecordUtils.buildCallTimeList(mCurCallTimeList)
                showListDialog(resultList, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        if (content != null && !TextUtils.isEmpty(content.second)) {
                            val target = BuildRecordUtils.getRecordByTarget(mCurCallTimeList, content.second)
                            updateUiByCallLogRecord(target)
                        }
                    }
                })
            }
        })
        bindData()
    }

    private fun updateUiByCallLogRecord(callLogRecord: CallLogRecord?) {
        tvTarget?.text = mContact?.flag + " _ "+ mContact?.relationship
        if (callLogRecord == null) {
            llData?.visibility = View.GONE
            flNodata?.visibility = View.VISIBLE
            return
        }
        llData?.visibility = View.VISIBLE
        flNodata?.visibility = View.GONE
        var resultStr : String
        var duration : String = ""
        var hasCallLog = false
        when(callLogRecord.type) {
            (CallLog.Calls.OUTGOING_TYPE) -> {
                if (callLogRecord.duration != null && callLogRecord.duration!! > 0) {
                    resultStr = resources.getString(R.string.take)
                    duration = callLogRecord.duration.toString()
                    hasCallLog = true
                } else {
                    resultStr = resources.getString(R.string.ring)
                    duration = resources.getString(R.string.no_take_record)
                }
            }
            else -> {
                resultStr = resources.getString(R.string.ring)
                duration = resources.getString(R.string.no_take_record)
            }
        }

        llPromiseTime?.isSelected = hasCallLog
        llPromiseTime?.isEnabled = hasCallLog
        llFeedBack?.isSelected = hasCallLog
        llFeedBack?.isEnabled = hasCallLog
        if (!hasCallLog) {
            tvFeedBack?.text = resources.getString(R.string.click_to_select)
            tvPromiseTime?.text = resources.getString(R.string.select_time)
            tvNote?.text = ""
            mSaveLogRequest.phone_connected = 0
        } else {
            mSaveLogRequest.phone_connected = 1
        }
        mSaveLogRequest.phone_time = BuildRecordUtils.convertMillionToStr(callLogRecord.date!!)

        tvResult?.text = resultStr
        tvCallTime?.text = BuildRecordUtils.convertMillionToStr(callLogRecord.date!!)
        tvTakeTime?.text = duration + "s"
        updateButtonState()
    }

    fun bindData() {

    }


}