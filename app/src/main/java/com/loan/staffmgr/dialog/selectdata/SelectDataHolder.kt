package com.loan.staffmgr.dialog.selectdata

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loan.staffmgr.R

class SelectDataHolder : RecyclerView.ViewHolder {

    var tvSelectData: TextView? = null

    constructor(itemView: View) : super(itemView) {
        tvSelectData = itemView.findViewById(R.id.tv_select_data)
    }

}