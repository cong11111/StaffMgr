package com.loan.staffmgr.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.collect.CallLogRecord
import com.loan.staffmgr.collect.CollectRecordLogMgr
import com.loan.staffmgr.collect.ReportCallLogMgr
import com.loan.staffmgr.global.App
import com.loan.staffmgr.global.ConfigMgr
import com.loan.staffmgr.global.Constant
import com.loan.staffmgr.ui.fragment.DashBoardFragment
import com.loan.staffmgr.ui.fragment.ticket.TicketFragment
import com.loan.staffmgr.ui.setting.SettingFragment
import com.loan.staffmgr.utils.log.LogSaver
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders
import java.util.ArrayList

class MainActivity : BaseActivity() {

    private var drawerLayout: DrawerLayout? = null
    private var settingFragment: SettingFragment? = null
    private var flLoading: View? = null

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
        ReportCallLogMgr.addCallBack(mCallBack)
    }

    private fun initData() {
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask< ArrayList<CallLogRecord>?>() {
            override fun doInBackground():  ArrayList<CallLogRecord>? {
                if (App.mContext != null) {
                   return CollectRecordLogMgr.readCallRecord(App.mContext!!)
                }
                return null
            }

            override fun onSuccess(result:  ArrayList<CallLogRecord>?) {
                CollectRecordLogMgr.setData(result)
                ReportCallLogMgr.uploadCallLog()
            }

        })
    }

    private fun initView() {
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout_container)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        val ivMainMenu = findViewById<ImageView>(R.id.iv_main_menu)
        flLoading = findViewById<View>(R.id.fl_sync_loading)
        val drawable = resources.getDrawable(R.drawable.ic_main_menu)
        DrawableCompat.setTint(drawable, resources.getColor(R.color.color_212121))
        ivMainMenu.setImageDrawable(drawable)
        ivMainMenu.setOnClickListener{
            drawerLayout?.openDrawer(GravityCompat.START)
        }
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
        LogSaver.logToFile("main activity")
        settingFragment = SettingFragment()
        replaceFragment(settingFragment!!, R.id.fl_main_setting)
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

    fun closeSlide() {
        drawerLayout?.closeDrawer(GravityCompat.START)
    }

    private val mCallBack = object : ReportCallLogMgr.CallBack {
        override fun onStart() {
            flLoading?.visibility = View.VISIBLE
        }

        override fun onEnd(isSuccess: Boolean, desc : String?) {
            flLoading?.visibility = View.GONE
            if (!isSuccess) {
                var resultStr = "Sync data failure"
                if (!TextUtils.isEmpty(desc)) {
                    resultStr = desc!!
                }
                ToastUtils.showShort(resultStr)
            } else {
                ToastUtils.showShort("Sync data success")
            }
        }

    }

    override fun onDestroy() {
        ReportCallLogMgr.removeAll()
        super.onDestroy()
    }
}