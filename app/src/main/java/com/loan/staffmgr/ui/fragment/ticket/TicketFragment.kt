package com.loan.staffmgr.ui.fragment.ticket

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.collect.ReportCallLogMgr
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.global.Constant
import com.loan.staffmgr.ui.CollectionLogActivity
import com.loan.staffmgr.ui.RecordActivity
import com.loan.staffmgr.ui.TicketListActivity
import com.loan.staffmgr.ui.fragment.BaseHomeFragment
import com.loan.staffmgr.ui.fragment.ticket.adapter.TicketAdapter
import com.loan.staffmgr.utils.BuildRecordUtils
import com.loan.staffmgr.utils.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.json.JSONObject

class TicketFragment : BaseHomeFragment() {
    private var mTicketLists : ArrayList<TicketsResponse> = ArrayList()
    private var mContactLists : ArrayList<TicketsResponse.Contact> = ArrayList()
    private var mUiContactLists : ArrayList<TicketsResponse.Contact> = ArrayList()

    private var mTicketAdapter : TicketAdapter? = null
    private var mCurPos = 0
    private var mActivityResultLauncher : ActivityResultLauncher<Intent>? = null

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
    private var tvT3Desc : AppCompatTextView? = null
    private var tvT3Copy : AppCompatTextView? = null
    private var tvLast : AppCompatTextView? = null
    private var tvNext : AppCompatTextView? = null
    private var tvPhoneNumCopy : AppCompatTextView? = null
    private var tvPhoneNumCall : AppCompatTextView? = null
    private var tvOrderIdCopy : AppCompatTextView? = null
    private var tvPageNum : AppCompatTextView? = null

    private var flRecord : FrameLayout? = null
    private var flCollectionLog : FrameLayout? = null
    private var rvContent : RecyclerView? = null
    private var scrollView : NestedScrollView? = null
    private var tvMyPhoneNum : AppCompatTextView? = null
    private var tvMyCallCount : AppCompatTextView? = null
    private var tvListTitle : AppCompatTextView? = null
    private var mRefreshLayout : SmartRefreshLayout? = null
    private var flLoading : FrameLayout? = null

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
        tvAppName = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_app_name)
        mRefreshLayout = view.findViewById<SmartRefreshLayout>(R.id.refresh_ticket)
        tvTicketId = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_ticket_id)
        tvName = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_name)
        tvAddrress = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_address)
        tvOrderId = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_order_id)
        tvDueDate = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_due_date)
        tvRepayAmount = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_repay_amount)
        tvPrincipal = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_principal)
        tvPenalty = view.findViewById<AppCompatTextView>(R.id.tv_ticket_1_penalty)
        tvListTitle = view.findViewById<AppCompatTextView>(R.id.tv_ticket_list)

        tvT3Desc = view.findViewById<AppCompatTextView>(R.id.tv_ticket3_desc)
        tvT3Copy = view.findViewById<AppCompatTextView>(R.id.tv_ticket3_va_link_copy)

        flRecord = view.findViewById<FrameLayout>(R.id.fl_ticket_record)
        tvMyPhoneNum = view.findViewById<AppCompatTextView>(R.id.tv_ticket_phone_number)
        tvMyCallCount = view.findViewById<AppCompatTextView>(R.id.tv_ticket2_result)

        flCollectionLog = view.findViewById<FrameLayout>(R.id.fl_ticket_collection_log)
        rvContent = view.findViewById<RecyclerView>(R.id.rv_ticket_content)

        tvLast = view.findViewById<AppCompatTextView>(R.id.tv_ticket_last)
        tvNext = view.findViewById<AppCompatTextView>(R.id.tv_ticket_next)
        tvPhoneNumCopy = view.findViewById<AppCompatTextView>(R.id.tv_phonenum_copy)
        tvPhoneNumCall = view.findViewById<AppCompatTextView>(R.id.tv_phonenum_call)
        tvOrderIdCopy = view.findViewById<AppCompatTextView>(R.id.tv_ticket1_orderid_copy)
        tvPageNum = view.findViewById<AppCompatTextView>(R.id.tv_ticket_pagenum)
        scrollView = view.findViewById<NestedScrollView>(R.id.nested_scroll_view)
        flLoading = view.findViewById<FrameLayout>(R.id.fl_ticket_loading)

        rvContent?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mTicketAdapter = TicketAdapter(mUiContactLists, this)
        rvContent?.adapter = mTicketAdapter

        flCollectionLog?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                val ticketsResponse = getTicketResponse()
                CollectionLogActivity.showMe(context!!, ticketsResponse?.ticket?.order_id)
            }

        })
        flRecord?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                val ticketResponse = getTicketResponse()
                RecordActivity.showMe(context!!, mContactLists, ticketResponse?.ticket)
            }

        })
        tvLast?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                mCurPos--
                if (mCurPos < 0) {
                    mCurPos = 0
                }
                scrollView?.smoothScrollTo(0,0)
                bindData()
            }

        })
        tvNext?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                mCurPos++
                if (mCurPos > mTicketLists.size - 1) {
                    mCurPos = mTicketLists.size - 1
                }
                scrollView?.smoothScrollTo(0,0)
                bindData()
            }

        })

        tvOrderIdCopy?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                val ticketsResponse = getTicketResponse()
                copyTextToClipBoard(ticketsResponse?.ticket?.order_id.toString())
            }

        })
        tvListTitle?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                if (context == null) {
                    return
                }
                Constant.mTicketLists.clear()
                Constant.mTicketLists.addAll(mTicketLists)
                val intent = TicketListActivity.getIntent(context!!)
                mActivityResultLauncher?.launch(intent)
            }

        })
        mRefreshLayout?.setEnableLoadMore(false)
        mRefreshLayout?.setEnableRefresh(true)
        mRefreshLayout?.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                requestTickets()
            }

        })
        bindData()
        mRefreshLayout?.autoRefresh()

        mActivityResultLauncher =  registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val intent: Intent? = it.data
            val pos = intent?.getIntExtra("pos", -1)
            if (pos == null || pos == -1) {
                ToastUtils.showShort("error data.")
                return@registerForActivityResult
            }
            mCurPos = pos
            scrollView?.smoothScrollTo(0,0)
            bindData()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updatePageNum() {
        tvPageNum?.text = (mCurPos + 1).toString() + "/" + mTicketLists.size.toString()
    }

    private fun requestTickets() {
        flLoading?.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        OkGo.post<String>(Api.TICKETS).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    mRefreshLayout?.finishRefresh()
                    flLoading?.visibility = View.GONE
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
                    ReportCallLogMgr.setData(tickets)
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    mRefreshLayout?.finishRefresh()
                    flLoading?.visibility = View.GONE
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "request  tickets failure = " + response.body())
                    }
                    ToastUtils.showShort("request  tickets failure")
                    handleError()
                }
            })

    }

    override fun bindData() {
        if (isDestroy()) {
            return
        }
        if (mTicketLists.size == 0) {
            handleError()
            return
        }
        val ticketResponse = getTicketResponse()

        if (ticketResponse == null) {
            handleError()
            return
        }

        // TODO测试逻辑
        if (BuildConfig.DEBUG && ticketResponse.contacts != null) {
            for (index in 0 until ticketResponse.contacts!!.size) {
                val temp = ticketResponse.contacts!![index]
                if (index == 0) {
                    temp.mobile = "18939440244"
                } else if (index == 1) {
                    temp.mobile = "18539253050"
                }
            }
        }

        updatePageNum()
        if (ticketResponse.ticket != null) {
            val ticket = ticketResponse.ticket
            tvAppName?.text = ticket?.app_name
            tvTicketId?.text = ticket?.ticket_id.toString() + "    " + ticket?.case_level.toString()
            tvName?.text = ticket?.name.toString()
            tvAddrress?.text = ticket?.address.toString()
            tvOrderId?.text = ticket?.order_id.toString()
            tvDueDate?.text = ticket?.due_date.toString() + "    " + ticket?.due_days + " DAYS"
            tvRepayAmount?.text = ticket?.repay_amount.toString()
            tvPrincipal?.text = ticket?.principal.toString()
            tvPenalty?.text = ticket?.penalty.toString()
        }
        if (!TextUtils.isEmpty(ticketResponse.va_link)) {
            val strLink = resources.getString(R.string.va_link)
            val name = ticketResponse.ticket?.name
            val str = String.format(strLink, name, ticketResponse.va_link)
            tvT3Desc?.text = str
            tvT3Copy?.setOnClickListener(object : OnClickListener {
                override fun onClick(v: View?) {
                    val text = ticketResponse.va_link
                    copyTextToClipBoard(text)
                }

            })
        }
        mContactLists.clear()
        if (ticketResponse != null && ticketResponse.contacts != null) {
            mContactLists.addAll(ticketResponse.contacts!!)
            mUiContactLists.clear()
            mUiContactLists.addAll(mContactLists)
            val firstItem = mUiContactLists.removeAt(0)
            bindFirstItem(firstItem)

            mTicketAdapter?.notifyDataSetChanged()
        }
    }

    private fun bindFirstItem(firstItem: TicketsResponse.Contact) {
        if (firstItem.mobile == null) {
            return
        }
        tvMyPhoneNum?.text = firstItem.mobile
        setCallCountByPhoneNum(firstItem.mobile!!, tvMyCallCount)
        tvPhoneNumCopy?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                copyTextToClipBoard(firstItem.mobile)
            }

        })
        tvPhoneNumCall?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                callPhoneNum(firstItem.mobile)
            }

        })
    }

    fun setCallCountByPhoneNum(mobile : String , tv : TextView?) {
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<Pair<Int, Int>?>() {
            override fun doInBackground(): Pair<Int, Int>? {

                return BuildRecordUtils.getCallCount(mobile)
            }

            override fun onSuccess(result: Pair<Int, Int>?) {
                if (result == null){
                    return
                }
                val hasCalledCount = result.first
                val totalCount = result.second
                tv?.text = "$hasCalledCount takes / $totalCount times"
            }

        })
    }

    private fun handleError() {
        Log.e(TAG, " request tickets error .")
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }

    private fun getTicketResponse() : TicketsResponse? {
        if (mTicketLists.size == 0){
            return null
        }
        return mTicketLists[mCurPos]
    }

    fun copyTextToClipBoard(text: String?) {
        if (TextUtils.isEmpty(text)) {
            return
        }
        ClipboardUtils.copyText(text)
        ToastUtils.showShort("Copy " + text + " to clipboard success")
    }

    @SuppressLint("MissingPermission")
    fun callPhoneNum(phoneNum: String?) {
        if (TextUtils.isEmpty(phoneNum)) {
            return
        }
        PermissionUtils.permission(Manifest.permission.CALL_PHONE).callback(object :
            PermissionUtils.SimpleCallback {
            override fun onGranted() {
                if (!TextUtils.isEmpty(phoneNum)) {
                    PhoneUtils.call(phoneNum!!)
                }
            }

            override fun onDenied() {

            }

        }).request()

    }
}