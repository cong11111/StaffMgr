package com.loan.staffmgr.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.BarUtils
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.ui.record.MobileFragment
import com.loan.staffmgr.ui.record.WhatAppFragment

class RecordActivity : BaseActivity() {


    private var llMobile : LinearLayout? = null
    private var llWhatapp : LinearLayout? = null
    private var ivBack : AppCompatImageView? = null
    private var vpContainer : ViewPager? = null

    private var isSelectMobile = true

    private var mMobileFragment : MobileFragment? = null
    private var mWhatAppFragment : WhatAppFragment? = null

    companion object {
        const val TAG = "RecordActivity"
        val mContactLists : ArrayList<TicketsResponse.Contact> = ArrayList()
        var mTicket : TicketsResponse.Ticket? = null

        fun showMe(context : Context, list : ArrayList<TicketsResponse.Contact>, ticket : TicketsResponse.Ticket?) {
            val intent = Intent(context, RecordActivity::class.java)
            mContactLists.clear()
            mContactLists.addAll(list)
            mTicket = ticket
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.bg_color))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_record)
        initView()
    }

    private fun initView() {
        llMobile = findViewById<LinearLayout>(R.id.ll_record_mobile)
        ivBack = findViewById<AppCompatImageView>(R.id.iv_record_back)
        llWhatapp = findViewById<LinearLayout>(R.id.ll_record_whatapp)
        vpContainer = findViewById<ViewPager>(R.id.vp_record_container)

        llMobile?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                isSelectMobile = true
                updateState()
            }
        })
        llWhatapp?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                isSelectMobile = false
                updateState()
            }
        })
        ivBack?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })
        vpContainer?.adapter = Adapter(supportFragmentManager)
        updateState()
    }

    private fun updateState() {
        if (isSelectMobile) {
            llMobile?.isSelected = true
            llWhatapp?.isSelected = false
            vpContainer?.setCurrentItem(0, false)
        } else {
            llMobile?.isSelected = false
            llWhatapp?.isSelected = true
            vpContainer?.setCurrentItem(1, false)

        }
    }

    fun getContactList() : ArrayList<TicketsResponse.Contact> {
        return mContactLists
    }

   inner class Adapter(fragmentMgr : FragmentManager) : FragmentPagerAdapter(fragmentMgr) {
       override fun getCount(): Int {
           return 2
       }

       override fun getItem(position: Int): Fragment {
           if (position == 0) {
               if (mMobileFragment == null) {
                   mMobileFragment = MobileFragment()
               }
               return mMobileFragment!!
           } else {
               if (mWhatAppFragment == null) {
                   mWhatAppFragment = WhatAppFragment()
               }
               return mWhatAppFragment!!
           }
       }
   }

}