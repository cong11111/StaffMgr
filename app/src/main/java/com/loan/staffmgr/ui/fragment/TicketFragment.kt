package com.loan.staffmgr.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import com.loan.staffmgr.R
import com.loan.staffmgr.base.BaseFragment
import com.loan.staffmgr.ui.RecordActivity

class TicketFragment : BaseHomeFragment() {
    override fun bindData() {

    }

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
        val btnTest = view.findViewById<Button>(R.id.btn_test)
        btnTest.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                RecordActivity.showMe(context!!)
            }

        })
    }
}