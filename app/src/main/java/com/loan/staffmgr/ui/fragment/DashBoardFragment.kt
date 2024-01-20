package com.loan.staffmgr.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.DashboardResponse

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
        tvDesc = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_desc)
        tvAssignFirst = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_assign_first)
        tvAssignReloan = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_assign_reloan)
        tvRepayFirst = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_repay_first)
        tvRepayReloan = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_repay_reloan)
        tvRateFirst = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_rate_first)
        tvRateReloan = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_rate_reloan)
        tvRank = view.findViewById<AppCompatTextView>(R.id.tv_dashboard_rank)
        bindData()

    }

    override fun bindData() {
        val data = getData() ?: return
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

}