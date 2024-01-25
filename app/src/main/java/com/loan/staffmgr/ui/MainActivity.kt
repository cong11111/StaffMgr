package com.loan.staffmgr.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ThreadUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.collect.CollectRecordLogMgr
import com.loan.staffmgr.global.App
import com.loan.staffmgr.global.ConfigMgr
import com.loan.staffmgr.global.Constant
import com.loan.staffmgr.ui.fragment.DashBoardFragment
import com.loan.staffmgr.ui.fragment.ticket.TicketFragment
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders

class MainActivity : BaseActivity() {

    companion object {
        const val TAG = "MainActivity"
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            val httpHeaders = HttpHeaders()
            httpHeaders.put("token", Constant.mToken)
            OkGo.getInstance().addCommonHeaders(httpHeaders)
            context.startActivity(intent)
        }
    }

    private var mDashBoardFragment : DashBoardFragment? = null
    private var mTicketFragment : TicketFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.bg_color))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_main)
        initView()
        switchFragment(0)
        initData()
    }

    private fun initData() {
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<String?>() {
            override fun doInBackground(): String? {
                if (App.mContext != null) {
                    CollectRecordLogMgr.readCallRecord(App.mContext!!)
                }
                return ""
            }

            override fun onSuccess(result: String?) {

            }

        })
    }

    private fun initView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId) {
                    (R.id.item1)-> {
                        switchFragment(0)
                    }
                    (R.id.item2)-> {
                        switchFragment(1)
                    }
                }
                return true
            }

        })
        ConfigMgr.requestConfig()
    }

    private fun switchFragment(index : Int) {
        if (index == 0) {
            if (mDashBoardFragment == null) {
                mDashBoardFragment = DashBoardFragment()
            }
            mDashBoardFragment?.bindData()
            replaceFragment(mDashBoardFragment!!, R.id.container)
        } else if (index == 1) {
            if (mTicketFragment == null) {
                mTicketFragment = TicketFragment()
            }
            mTicketFragment?.bindData()
            replaceFragment(mTicketFragment!!, R.id.container)
        }
    }

}