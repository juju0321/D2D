package com.mamingjuju.d2d

import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.empty_list_layout.view.*

class EmptyListViewHolder(itemView: View): BaseViewHolder(itemView) {
    fun bind(emptyDisplayText: String) {
        Log.i("EmptyListViewHolder", "$emptyDisplayText")
        itemView.emptyListTextView.text = emptyDisplayText
    }
}