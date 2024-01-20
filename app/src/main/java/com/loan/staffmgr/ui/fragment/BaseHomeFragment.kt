package com.loan.staffmgr.ui.fragment

import com.loan.staffmgr.base.BaseFragment
import com.loan.staffmgr.bean.DashboardResponse
import com.loan.staffmgr.ui.MainActivity

abstract class BaseHomeFragment : BaseFragment() {

    fun getData() : DashboardResponse? {
        if (activity is MainActivity) {
            val mainActivity = activity as MainActivity
           return mainActivity.mDashboardResponse
        }
        return null
    }

    abstract fun bindData()
}