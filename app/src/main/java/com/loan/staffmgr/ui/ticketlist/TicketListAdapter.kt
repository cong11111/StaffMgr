package com.loan.staffmgr.ui.ticketlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.TicketsResponse
import com.loan.staffmgr.utils.MyDateUtils

class TicketListAdapter(val list : ArrayList<TicketsResponse>) :  RecyclerView.Adapter<ViewHolder>() {

    companion object {
        val TYPE_1 = 111
        val TYPE_2 = 112
    }

    var isPtpMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_1){
            return TicketTitleHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ticketlist_title, parent, false))
        } else {
            return TicketDescHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ticketlist_normal, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is TicketTitleHolder) {
            bindTitleHolder(holder)
        } else if (holder is TicketDescHolder){
            bindDescHolder(holder, position)
        }
    }

    private var mListener : OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private fun bindDescHolder(holder: TicketDescHolder, position: Int) {
        val temp = list[position]
        val ticket = temp.ticket
        if (ticket == null) {
            return
        }
        holder.tv1?.text = ticket.ticket_id.toString()
        holder.tv2?.text = ticket.repay_amount.toString()
        if (isPtpMode) {
            holder.tv3?.text = ticket.promise_repay_date.toString()
            try {
                //若PTP时间在未来一小时以上，显示为绿色；
                //若PTP时间在未来一小时，显示为黄色；
                //若PTP时间已超过当前时间，显示为红色；
                val delta = MyDateUtils.convertStrToMillions(ticket.promise_repay_date!!) - System.currentTimeMillis()
                if (delta < 0) {
                    holder.tv3?.setTextColor(Color.RED)
                } else if (delta > 0 && delta < 60 * 60 * 1000) {
                    holder.tv3?.setTextColor(Color.YELLOW)
                } else if (delta > 60 * 60 * 1000) {
                    holder.tv3?.setTextColor(Color.GREEN)
                }
            } catch (e : java.lang.Exception) {

            }
        } else {
            holder.tv3?.text = ticket.due_date.toString()
        }
        val isComplete = (ticket.status == 5 || ticket.status == 6)
        holder.tv4?.isEnabled = !isComplete
        holder.tv4?.isSelected = !isComplete
        holder.tv4?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                mListener?.click(position)
            }

        })
    }

    private fun bindTitleHolder(holder: TicketTitleHolder) {
        holder.tv1?.text = "Ticket Id:"
        holder.tv2?.text = "Amount:"
        if (isPtpMode) {
            holder.tv3?.text = "Ptp time:"
        } else {
            holder.tv3?.text = "Last collection time:"
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0){
            return TYPE_1
        } else {
            return TYPE_2
        }
    }

    class TicketTitleHolder(itemView : View) : RecyclerView.ViewHolder(itemView)  {

        var tv1 : AppCompatTextView? = null
        var tv2 : AppCompatTextView? = null
        var tv3 : AppCompatTextView? = null

        init {
            tv1 =  itemView.findViewById<AppCompatTextView>(R.id.tv_1)
            tv2 =  itemView.findViewById<AppCompatTextView>(R.id.tv_2)
            tv3 =  itemView.findViewById<AppCompatTextView>(R.id.tv_3)
        }
    }

    class TicketDescHolder(itemView : View) : RecyclerView.ViewHolder(itemView)  {
        var tv1 : AppCompatTextView? = null
        var tv2 : AppCompatTextView? = null
        var tv3 : AppCompatTextView? = null
        var tv4 : AppCompatTextView? = null

        init {
            tv1 =  itemView.findViewById<AppCompatTextView>(R.id.tv_1)
            tv2 =  itemView.findViewById<AppCompatTextView>(R.id.tv_2)
            tv3 =  itemView.findViewById<AppCompatTextView>(R.id.tv_3)
            tv4 =  itemView.findViewById<AppCompatTextView>(R.id.tv_4)
        }
    }

    interface OnItemClickListener {
        fun click(pos : Int)
    }
}