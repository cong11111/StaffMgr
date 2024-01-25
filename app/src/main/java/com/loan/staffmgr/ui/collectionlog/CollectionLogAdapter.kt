package com.loan.staffmgr.ui.collectionlog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blankj.utilcode.util.AppUtils
import com.loan.staffmgr.R
import com.loan.staffmgr.bean.CollectionLogResponse
import com.loan.staffmgr.global.Constant
import com.loan.staffmgr.utils.BuildRecordUtils

class CollectionLogAdapter(val mList: ArrayList<CollectionLogResponse>) : RecyclerView.Adapter<CollectionLogAdapter.CollectionLogHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionLogHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_collection_log, parent, false)
        return CollectionLogHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: CollectionLogHolder, position: Int) {
        val collectionLog = mList[position]
        holder.tvTitle1?.text = "Name:"
        holder.tvTitle2?.text = "CreateTime:"
        holder.tvTitle3?.text = "CallResult:"
        holder.tvTitle4?.text = "FeedBack:"
        holder.tvTitle5?.text = "Notes:"

        holder.tvDesc1?.text = Constant.mName
        if (collectionLog.Ctime != null) {
            holder.tvDesc2?.text = BuildRecordUtils.convertMillionToStr(collectionLog.Ctime!!)
        }
        if (collectionLog.PhoneConnect == 0) {
            holder.tvDesc3?.text = holder.itemView.context.getString(R.string.take)
        } else {
            holder.tvDesc3?.text = holder.itemView.context.getString(R.string.ring)
        }

        holder.tvDesc4?.text = collectionLog?.Result
        holder.tvDesc5?.text = collectionLog?.Remark
    }

    class CollectionLogHolder(itemView : View) : ViewHolder(itemView) {
        var tvTitle1 : TextView? = null
        var tvTitle2 : TextView? = null
        var tvTitle3 : TextView? = null
        var tvTitle4 : TextView? = null
        var tvTitle5 : TextView? = null
        var tvDesc1 : TextView? = null
        var tvDesc2 : TextView? = null
        var tvDesc3 : TextView? = null
        var tvDesc4 : TextView? = null
        var tvDesc5 : TextView? = null
        init {
            tvTitle1 = itemView.findViewById<TextView>(R.id.tv_title_1)
            tvTitle2 = itemView.findViewById<TextView>(R.id.tv_title_2)
            tvTitle3 = itemView.findViewById<TextView>(R.id.tv_title_3)
            tvTitle4 = itemView.findViewById<TextView>(R.id.tv_title_4)
            tvTitle5 = itemView.findViewById<TextView>(R.id.tv_title_5)

            tvDesc1 = itemView.findViewById<TextView>(R.id.tv_desc_1)
            tvDesc2 = itemView.findViewById<TextView>(R.id.tv_desc_2)
            tvDesc3 = itemView.findViewById<TextView>(R.id.tv_desc_3)
            tvDesc4 = itemView.findViewById<TextView>(R.id.tv_desc_4)
            tvDesc5 = itemView.findViewById<TextView>(R.id.tv_desc_5)

        }
    }
}