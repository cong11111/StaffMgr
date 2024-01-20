package com.loan.staffmgr.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.AdapterView.OnItemSelectedListener
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.loan.staffmgr.BuildConfig
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.DashboardResponse
import com.loan.staffmgr.collect.CollectRecordLogMgr
import com.loan.staffmgr.global.Api
import com.loan.staffmgr.global.App
import com.loan.staffmgr.global.Constant
import com.loan.staffmgr.ui.fragment.DashBoardFragment
import com.loan.staffmgr.ui.fragment.TicketFragment
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.io.File

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

    var mDashboardResponse : DashboardResponse? = null

    private var mDashBoardFragment : DashBoardFragment? = null
    private var mTicketFragment : TicketFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.theme_color))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_main)
        initView()
        requestDashboard()
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
                return false
            }

        })
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

    private fun requestDashboard() {
        val jsonObject = JSONObject()
        OkGo.post<String>(Api.DASH_BOARD).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val dashboardResponse: DashboardResponse? =
                        checkResponseSuccess(response, DashboardResponse::class.java)
                    if (dashboardResponse == null) {
                        Log.e(TAG, " request dash board error ." + response.body())
                        return
                    }
                    mDashboardResponse = dashboardResponse
                    mDashBoardFragment?.bindData()
                    mTicketFragment?.bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    if (isFinishing|| isDestroyed) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "request dash board failure = " + response.body())
                    }
                    ToastUtils.showShort("request dash board failure")
                }
            })

    }
}