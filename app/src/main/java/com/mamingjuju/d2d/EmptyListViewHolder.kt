package com.mamingjuju.d2d

import android.view.View
import kotlinx.android.synthetic.main.empty_list_layout.view.*

class EmptyListViewHolder(itemView: View): BaseViewHolder(itemView) {
    fun bind(emptyDisplayText: String, onEmptyListImageClicked: () -> Unit, addingNewPassengerIsEnabled: Boolean) {
        itemView.emptyListTextView.text = emptyDisplayText
        if(addingNewPassengerIsEnabled) {
            itemView.emptyListImageViewAdd.setOnClickListener {
                onEmptyListImageClicked()
            }
        }
    }
}