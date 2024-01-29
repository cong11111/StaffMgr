package com.loan.staffmgr.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ToastUtils
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.DashboardResponse
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.ui.MainActivity
import com.loan.staffmgr.utils.CheckResponseUtils.Companion.checkResponseSuccess
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.json.JSONObject

class DashBoardFragment : BaseHomeFragment() {

    private var tvTitle : AppCompatTextView? = null
    private var tvDesc : AppCompatTextView? = null
    private var tvAssignFirst : AppCompatTextView? = null
    private var tvAssignReloan : AppCompatTextView? = null
    private var tvRepayFirst : AppCompatTextView? = null
    private var tvRepayReloan : AppCompatTextView? = null
    private var tvRateFirst : AppCompatTextView? = null
    private var tvRateReloan : AppCompatTextView? = null
    private var tvRank : AppCompatTextView? = null
    private var mRefreshLayout : SmartRefreshLayout? = null
    private var flLoading : View? = null

    var mDashboardResponse : DashboardResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_name)
        mRefreshLayout = view.findViewById<SmartRefreshLayout>(R.id.refresh_dash_board)
        tvDesc = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_desc)
        tvAssignFirst = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_assign_first)
        tvAssignReloan = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_assign_reloan)
        tvRepayFirst = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_repay_first)
        tvRepayReloan = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_repay_reloan)
        tvRateFirst = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_rate_first)
        tvRateReloan = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_rate_reloan)
        tvRank = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_rank)
        flLoading = view.findViewById<View>(R.id.fl_dash_board_loading)

        mRefreshLayout?.setEnableLoadMore(false)
        mRefreshLayout?.setEnableRefresh(true)
        mRefreshLayout?.setHeaderTriggerRate(3f)
        mRefreshLayout?.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                requestDashboard()
            }

        })
        bindData()
        mRefreshLayout?.autoRefresh()
    }

    override fun bindData() {
        val data = mDashboardResponse ?: return
        val name = data.account?.name
        val group = data.account?.group
        if (!TextUtils.isEmpty(name)) {
            tvTitle?.text = name
        }
        if (!TextUtils.isEmpty(group)) {
            tvDesc?.text = group
        }
        updateFirstReloan(data.collection?.assign_amount, tvAssignFirst, tvAssignReloan)
        updateFirstReloan(data.collection?.repay_amount, tvRepayFirst, tvRepayReloan)
        updateFirstReloan(data.collection?.repay_amount, tvRateFirst, tvRateReloan)
        val rank = data.collection?.current_rank?.rank
        tvRank?.text = rank
    }

    private fun updateFirstReloan(amount : DashboardResponse.Amount?, tv1 : AppCompatTextView? , tv2 : AppCompatTextView?) {
        val firstLoan = amount?.first_loan
        val reloan = amount?.reloan
        if (!TextUtils.isEmpty(firstLoan)) {
            tv1?.text = firstLoan
        }
        if (!TextUtils.isEmpty(reloan)) {
            tv2?.text = reloan
        }
    }

    private fun requestDashboard() {
        flLoading?.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        OkGo.post<String>(Api.DASH_BOARD).tag(MainActivity.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    flLoading?.visibility = View.GONE
                    mRefreshLayout?.finishRefresh()
                    val dashboardResponse: DashboardResponse? =
                        checkResponseSuccess(response, DashboardResponse::class.java)
                    if (dashboardResponse == null) {
                        Log.e(MainActivity.TAG, " request dash board error ." + response.body())
                        return
                    }
                    mDashboardResponse = dashboardResponse
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    flLoading?.visibility = View.GONE
                    mRefreshLayout?.finishRefresh()
                    if (BuildConfig.DEBUG) {
                        Log.e(MainActivity.TAG, "request dash board failure = " + response.body())
                    }
                    ToastUtils.showShort("request dash board failure")
                }
            })

    }
}