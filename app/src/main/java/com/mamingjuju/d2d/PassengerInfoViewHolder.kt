package com.mamingjuju.d2d

import android.view.View
import kotlinx.android.synthetic.main.booking_detail.view.*

class PassengerInfoViewHolder(itemView: View): BaseViewHolder(itemView) {
    fun bind(mapOfPassengerInformationToPrimaryKey:  Pair<Long, PassengerInformation>,
             showHeader: Boolean,
             cardViewClickListener: (Long, PassengerInformation) -> Unit,
             removeButtonClickListener: (Long) -> Unit,
             onCallButtonClicked: (String) -> Unit) {

        if(showHeader) {
            itemView.headerContainer.visibility = View.VISIBLE
            itemView.textViewHeaderBookingDate.text = mapOfPassengerInformationToPrimaryKey.second.bookingDate
        }
        else {
            itemView.headerContainer.visibility = View.GONE
        }

        itemView.textViewPassengerName.text = mapOfPassengerInformationToPrimaryKey.second.name
        itemView.textViewContactNumber.text = mapOfPassengerInformationToPrimaryKey.second.number
        itemView.textViewOrigin.text = mapOfPassengerInformationToPrimaryKey.second.origin
        itemView.textViewDestination.text = mapOfPassengerInformationToPrimaryKey.second.destination
        itemView.callButton.setOnClickListener {
            if (mapOfPassengerInformationToPrimaryKey.first != null) {
                onCallButtonClicked("tel:${mapOfPassengerInformationToPrimaryKey.second.number}")
            }
        }
        itemView.removeButton.setOnClickListener {
            if (mapOfPassengerInformationToPrimaryKey.first != null) {
                removeButtonClickListener(mapOfPassengerInformationToPrimaryKey.first)
            }
        }
        itemView.setOnClickListener {
            if (mapOfPassengerInformationToPrimaryKey.first != null) {
                cardViewClickListener(mapOfPassengerInformationToPrimaryKey.first, mapOfPassengerInformationToPrimaryKey.second)
            }
        }
    }
}