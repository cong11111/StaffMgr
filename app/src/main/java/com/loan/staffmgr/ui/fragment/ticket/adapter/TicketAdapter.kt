package com.loan.staffmgr.ui.fragment.ticket.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.ui.fragment.ticket.TicketFragment

class TicketAdapter(val contactLists: ArrayList<TicketsResponse.Contact>, val fragment: TicketFragment?) : RecyclerView.Adapter<TicketHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket_2, parent, false)
        return TicketHolder(view)
    }

    override fun getItemCount(): Int {
        return contactLists.size
    }

    override fun onBindViewHolder(holder: TicketHolder, position: Int) {
        val contact = contactLists[position]
        holder.tvCall?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                if (!TextUtils.isEmpty(contact.mobile)) {
                    fragment?.callPhoneNum(contact.mobile)
                }
            }

        })
        holder.tvId?.text = contact?.key.toString()
        holder.tvRelationShip?.text =  contact?.relationship.toString()
        holder.tvName?.text = contact?.flag.toString()
        if (!TextUtils.isEmpty(contact.mobile)) {
            fragment?.setCallCountByPhoneNum(contact.mobile!!, holder.tvCount)
        }
    }
}