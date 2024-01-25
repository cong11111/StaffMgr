package com.loan.staffmgr.ui.fragment.ticket.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.loan.staffmgr.R

class TicketHolder(itemView : View) : ViewHolder(itemView)  {

    var tvCall : AppCompatTextView? = null
    var tvId : AppCompatTextView? = null
    var tvRelationShip : AppCompatTextView? = null
    var tvName : AppCompatTextView? = null
    var tvCount : AppCompatTextView? = null

    init {
        tvCall =  itemView.findViewById<AppCompatTextView>(R.id.tv_ticket_item_call)
        tvId =  itemView.findViewById<AppCompatTextView>(R.id.tv_ticket_item_id)
        tvRelationShip =  itemView.findViewById<AppCompatTextView>(R.id.tv_ticket_item_relationship)
        tvName =  itemView.findViewById<AppCompatTextView>(R.id.tv_ticket_item_name)
        tvCount =  itemView.findViewById<AppCompatTextView>(R.id.tv_ticket_item_count)
    }
}