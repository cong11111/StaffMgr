package com.loan.staffmgr.ui.fragment

import com.loan.staffmgr.base.BaseFragment
import com.loan.staffmgr.bean.DashboardResponse
import com.loan.staffmgr.ui.MainActivity

abstract class BaseHomeFragment : BaseFragment() {

    abstract fun bindData()
}