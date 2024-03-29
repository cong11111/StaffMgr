package com.loan.staffmgr.ui.record

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseFragment
import com.loan.staffmgr.bean.SaveLogRequest
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.dialog.selectdata.SelectDataDialog
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.global.ConfigMgr
import com.loan.staffmgr.ui.RecordActivity
import com.loan.staffmgr.utils.BuildRecordUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.util.*

abstract class BaseSubmitFragment : BaseFragment() {

    val mSaveLogRequest = SaveLogRequest()
    var mContact : TicketsResponse.Contact? = null

    var flSubmit : View? = null
    var llTarget : LinearLayout? = null
    var tvTarget : AppCompatTextView? = null
    var tvFeedBack : AppCompatTextView? = null
    var llFeedBack : LinearLayout? = null
    var llPromiseTime : LinearLayout? = null
    var tvPromiseTime : TextView? = null
    private var viewNote : View? = null
    var tvNote : TextView? = null

    private var mActivityResultLauncher : ActivityResultLauncher<Intent>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化数据
        mSaveLogRequest.ticket_id = RecordActivity.mTicket?.ticket_id
        mSaveLogRequest.communication_way =
            if (this@BaseSubmitFragment is MobileFragment) { 2 } else { 1 }


        flSubmit = view.findViewById<View>(R.id.fl_record_submit)
        llTarget = view.findViewById<LinearLayout>(R.id.ll_target_container)
        tvTarget = view.findViewById<AppCompatTextView>(R.id.tv_record_target)
        tvFeedBack = view.findViewById<AppCompatTextView>(R.id.tv_record_feedback)
        llFeedBack = view.findViewById<LinearLayout>(R.id.ll_feedback_container)
        llPromiseTime = view.findViewById<LinearLayout>(R.id.ll_promise_time_container)
        tvPromiseTime = view.findViewById<TextView>(R.id.tv_promise_time_container)
        viewNote = view.findViewById<View>(R.id.cl_edit_note_item)
        tvNote = view.findViewById<TextView>(R.id.et_diary_edit_note_content)

        flSubmit?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!checkButtonState(true)) {
                    return
                }

                submitSaveRecord(mSaveLogRequest)
            }
        })
        llFeedBack?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val resultList = BuildRecordUtils.buildFeedbackList()
                showListDialog(resultList, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        val dataStr = content?.first.toString()
                        if (TextUtils.equals(dataStr, "Willing to pay")) {
                            mSaveLogRequest.repay_inclination = 1
                        } else {
                            mSaveLogRequest.repay_inclination = 2
                        }
                        tvFeedBack?.text = dataStr
                        mSaveLogRequest.result = dataStr
//                        mSaveLogRequest.overdue_reason_item = pos
                        updateButtonState()
                    }
                })
            }
        })
        llPromiseTime?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                showTimePicker(object : OnTimeSelectListener {
                    override fun onTimeSelect(date: Date?, v: View?) {
                        if (date == null) {
                            return
                        }
                        val phoneTimeStr = BuildRecordUtils.convertMillionToStr(date.time)
                        tvPromiseTime?.text = phoneTimeStr
                        // 打电话时间
                        mSaveLogRequest.promise_repay_time = phoneTimeStr
                        updateButtonState()
                    }

                })
            }
        })
        viewNote?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (mContact == null) {
                    ToastUtils.showShort("unselect target.")
                    return
                }
                if (context == null) {
                    return
                }
                val intent = InputActivity.getIntent(context!!, mSaveLogRequest.remark)
                mActivityResultLauncher?.launch(intent)

            }
        })

        mActivityResultLauncher =  registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val intent: Intent? = it.data
            val data = intent?.getStringExtra("data")
            if (data == null && TextUtils.isEmpty(mSaveLogRequest.remark)) {
                ToastUtils.showShort("not input data.")
                return@registerForActivityResult
            }
            tvNote?.text = data
            mSaveLogRequest.remark = data
        }
        updateButtonState()
    }

    private fun submitSaveRecord(saveLogRequest: SaveLogRequest) {
        val jsonObject = JSONObject()
        jsonObject.put( "ticket_id", saveLogRequest.ticket_id)
        jsonObject.put( "result", saveLogRequest.result)
        jsonObject.put( "remark", saveLogRequest.remark)
        jsonObject.put( "communication_way", saveLogRequest.communication_way)
        jsonObject.put( "phone_time", saveLogRequest.phone_time)
        jsonObject.put( "phone_objects", saveLogRequest.phone_objects)
        jsonObject.put( "phone_connected", saveLogRequest.phone_connected)
        jsonObject.put( "repay_inclination", saveLogRequest.repay_inclination)
        jsonObject.put( "promise_repay_time", saveLogRequest.promise_repay_time)
        jsonObject.put( "overdue_reason_item", saveLogRequest.overdue_reason_item)
        jsonObject.put( "phone_object_mobile", saveLogRequest.phone_object_mobile)

        OkGo.post<String>(Api.SAVE_TICKET_LOG).tag(RecordActivity.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    ToastUtils.showShort("request save record success")
                    activity?.finish()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(RecordActivity.TAG, "request save record failure = " + response.body())
                    }
                    ToastUtils.showShort("request save record failure")
                }
            })
    }

    fun updateButtonState(showToast : Boolean = false) {
        if (mSaveLogRequest.repay_inclination == 1) {
            llPromiseTime?.visibility = View.VISIBLE
            llPromiseTime?.isSelected = true
            llPromiseTime?.isEnabled = true
        } else {
            llPromiseTime?.visibility = View.GONE
            llPromiseTime?.isSelected = false
            llPromiseTime?.isEnabled = false
        }
        val isSelect = checkButtonState(showToast)
        flSubmit?.isEnabled = isSelect
    }

    fun checkButtonState(showToast : Boolean = false) : Boolean{
        if (mSaveLogRequest.phone_time == null) {
            if (showToast) {
                if (mSaveLogRequest.communication_way == 2) {
                    ToastUtils.showShort("unselect phone time.")
                } else {
                    ToastUtils.showShort("unselect contact record.")
                }
            }
            return false
        }
        when (mSaveLogRequest.communication_way) {
            (2) -> {
                if (mSaveLogRequest.phone_connected == null) {
                    if (showToast) {
                        ToastUtils.showShort("unselect contact record.")
                    }
                    return false
                }
                if (mSaveLogRequest.phone_connected == ConfigMgr.getPhoneConnect(true).toInt()) {
                    if (mSaveLogRequest.repay_inclination == 1 && mSaveLogRequest.promise_repay_time == null) {
                        if (showToast) {
                            ToastUtils.showShort("unselect promise time.")
                        }
                        return false
                    }
                    if ( mSaveLogRequest.result == null) {
                        if (showToast) {
                            ToastUtils.showShort("unselect feedback.")
                        }
                        return false
                    }
                }
            }
            // what app
            (1) -> {
                if (mSaveLogRequest.repay_inclination == 1 && mSaveLogRequest.promise_repay_time == null) {
                    if (showToast) {
                        ToastUtils.showShort("unselect promise time.")
                    }
                    return false
                }
                if ( mSaveLogRequest.result == null) {
                    if (showToast) {
                        ToastUtils.showShort("unselect feedback.")
                    }
                    return false
                }
            }
        }

        return true
    }

    fun showListDialog(
        list: ArrayList<Pair<String, String>>,
        observer: SelectDataDialog.Observer
    ) {
        if (context == null) {
            return
        }
        val dialog = SelectDataDialog(requireContext())
        val tempList = ArrayList<Pair<String, String>>()
        tempList.addAll(list)
        dialog.setList(tempList, observer)
        dialog.show()
    }

    fun showTimePicker(listener: OnTimeSelectListener?) {
        if (context == null) {
            return
        }
//        if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
//            KeyboardUtils.hideSoftInput(requireActivity())
//        }
        //时间选择器
        val booleanArray = booleanArrayOf(true, true, true, true, true, true)
        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()
        endCalendar.set(2100,12,31)
        val pvTime = TimePickerBuilder(context, listener)
            .setRangDate(startCalendar, endCalendar)
            .setType(booleanArray)
            .setSubmitText("ok")
            .setCancelText("cancel").build()
        pvTime.show()
    }


}