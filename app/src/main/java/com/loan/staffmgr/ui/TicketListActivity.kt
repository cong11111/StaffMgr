package com.loan.staffmgr.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseActivity
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.global.Constant
import com.loan.staffmgr.ui.ticketlist.TicketListAdapter
import com.loan.staffmgr.utils.MyDateUtils
import org.angmarch.views.NiceSpinner
import org.angmarch.views.OnSpinnerItemSelectedListener
import java.util.Objects

class TicketListActivity : BaseActivity() {

    private var ivBack: AppCompatImageView? = null
    private var rvContent: RecyclerView? = null
    private var niceSpinner: NiceSpinner? = null

    private val mShowTicketLists : ArrayList<TicketsResponse> = ArrayList()
    private var mAdapter : TicketListAdapter? = null
    companion object {
        fun getIntent(context : Context) : Intent {
            val intent = Intent(context, TicketListActivity::class.java)
            return intent
//            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_list)
        initView()
    }

    private fun initView() {
        ivBack = findViewById<AppCompatImageView>(R.id.iv_ticket_back)
        rvContent = findViewById<RecyclerView>(R.id.rv_ticket_list_content)
        niceSpinner = findViewById<NiceSpinner>(R.id.spinner_ticket_type)
        niceSpinner?.attachDataSource(buildDataList())
        niceSpinner?.setOnSpinnerItemSelectedListener(object : OnSpinnerItemSelectedListener {
            override fun onItemSelected(
                parent: NiceSpinner?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    (0) -> {
                        Constant.mTicketStatus = -1
                        mAdapter?.isPtpMode = false
                    }
                    (1) -> {
                        Constant.mTicketStatus = 6
                        mAdapter?.isPtpMode = true
                    }
                    (2) -> {
                        Constant.mTicketStatus = 4
                        mAdapter?.isPtpMode = false
                    }
                    (3) -> {
                        Constant.mTicketStatus = 5
                        mAdapter?.isPtpMode = false
                    }
                    (4) -> {
                        Constant.mTicketStatus = 3
                        mAdapter?.isPtpMode = false
                    }
                }
                updateTicketList()
            }

        })
        mAdapter = TicketListAdapter(mShowTicketLists)
        rvContent?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvContent?.adapter = mAdapter
        updateTicketList()
    }

    private fun updateTicketList() {
        mShowTicketLists.clear()
        mShowTicketLists.add(TicketsResponse())
        when(Constant.mTicketStatus) {
            3 -> {  //Progressing 进行中
                mShowTicketLists.addAll(getShowTicketList(3))
            }
            5 -> {  // Closed 关闭
                mShowTicketLists.addAll(getShowTicketList(5))
            }
            4 -> {  // Cleared 结清
                mShowTicketLists.addAll(getShowTicketList(4))
            }
            6 -> {  //Progressing-PTP 承诺还款单 部分完成, 当前时间和promise repay date比较
                mShowTicketLists.addAll(getShowTicketList(6, true))
            }
            else -> {
                mShowTicketLists.addAll(Constant.mTicketLists)
            }
        }
        mAdapter?.setOnItemClickListener(object : TicketListAdapter.OnItemClickListener {
            override fun click(pos: Int) {
                val intent = Intent()
                intent.putExtra("pos", pos - 1)
                setResult(1111, intent)
                finish()
            }

        })
        mAdapter?.notifyDataSetChanged()
    }

    private fun getShowTicketList(status : Int, needComparePromise : Boolean = false) : ArrayList<TicketsResponse>{
        val mTicketLists : ArrayList<TicketsResponse> = ArrayList()
        for (index in 0 until Constant.mTicketLists.size) {
            val ticket = Constant.mTicketLists[index]
            if (ticket.ticket == null){
                continue
            }
            var canAdd = false
            if (ticket.ticket?.status == status){
                if (needComparePromise){
                    try {
                        val str = ticket.ticket!!.promise_repay_date
                        if (TextUtils.equals(str, "-") || TextUtils.isEmpty(str)){
                            canAdd = true
                        } else {
                            if (MyDateUtils.convertStrToMillions(str!!) - System.currentTimeMillis() > 0) {
                                canAdd = true
                            }
                        }
                    } catch (e : java.lang.Exception) {

                    }

                } else {
                    canAdd = true
                }
            }
            if (canAdd){
                mTicketLists.add(ticket)
            }
        }
        return mTicketLists
    }

    private fun buildDataList() : ArrayList<String> {
        val list = ArrayList<String>()
        list.add(resources.getString(R.string.all))
        list.add(resources.getString(R.string.progressing_ptp))
        list.add(resources.getString(R.string.cleared))
        list.add(resources.getString(R.string.closed))
        list.add(resources.getString(R.string.progressing))
        return list
    }
}