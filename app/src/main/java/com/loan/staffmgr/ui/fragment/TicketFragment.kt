package com.loan.staffmgr.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.ui.RecordActivity
import com.loan.staffmgr.utils.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class TicketFragment : BaseHomeFragment() {
    private var mTicketLists : ArrayList<TicketsResponse> = ArrayList()

    companion object {
        const val TAG = "TicketFragment"

    }

    private var tvAppName : AppCompatTextView? = null
    private var tvTicketId : AppCompatTextView? = null
    private var tvName : AppCompatTextView? = null
    private var tvAddrress : AppCompatTextView? = null
    private var tvOrderId : AppCompatTextView? = null
    private var tvDueDate : AppCompatTextView? = null
    private var tvRepayAmount : AppCompatTextView? = null
    private var tvPrincipal : AppCompatTextView? = null
    private var tvPenalty : AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ticket, container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnTest = view.findViewById<Button>(R.id.btn_test)
        tvAppName = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_app_name)
        tvTicketId = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_ticket_id)
        tvName = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_name)
        tvAddrress = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_address)
        tvOrderId = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_order_id)
        tvDueDate = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_due_date)
        tvRepayAmount = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_repay_amount)
        tvPrincipal = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_principal)
        tvPenalty = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_penalty)

        btnTest.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                RecordActivity.showMe(context!!)
            }

        })
        requestTickets()
    }

    private fun requestTickets() {
        val jsonObject = JSONObject()
        OkGo.post<String>(Api.TICKETS).tag(TAG)
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
                    val tickets = com.alibaba.fastjson.JSONObject.parseArray(responseStr, TicketsResponse::class.java)
                    if (tickets == null || tickets.isEmpty()) {
                        handleError()
                        return
                    }
                    mTicketLists.clear()
                    mTicketLists.addAll(tickets)
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "request  tickets failure = " + response.body())
                    }
                    ToastUtils.showShort("request  tickets failure")
                    handleError()
                }
            })

    }

    override fun bindData() {
        if (mTicketLists.size == 0) {
            handleError()
            return
        }
        val ticketResponse = mTicketLists.get(0)
        if (ticketResponse == null) {
            handleError()
            return
        }
        if (ticketResponse.ticket != null) {
            val ticket = ticketResponse.ticket
            tvAppName?.text = ticket?.app_name
            tvTicketId?.text = ticket?.ticket_id.toString()
            tvName?.text = ticket?.name.toString()
            tvAddrress?.text = ticket?.address.toString()
            tvOrderId?.text = ticket?.order_id.toString()
            tvDueDate?.text = ticket?.due_date.toString()
            tvRepayAmount?.text = ticket?.repay_amount.toString()
            tvPrincipal?.text = ticket?.principal.toString()
            tvPenalty?.text = ticket?.penalty.toString()
        }

    }

    private fun handleError() {
        Log.e(TAG, " request tickets error .")
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}