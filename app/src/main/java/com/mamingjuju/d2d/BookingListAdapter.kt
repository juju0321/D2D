package com.mamingjuju.d2d

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.util.toAndroidPair
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.booking_detail.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PassengerInformationWithKey(
    val key: Long,
    val name: String,
    val number: String,
    val origin: String,
    val destination: String
)

@RequiresApi(Build.VERSION_CODES.O)
class BookingListAdapter(myDataSet: Map<Long, PassengerInformation>,
                         private val editButtonClickListener: (Long, PassengerInformation) -> Unit,
                         private val removeButtonClickListener: (Int, Long) -> Unit): RecyclerView.Adapter<BookingListAdapter.ViewHolder>() {

    private var mDataSet = myDataSet.toList().sortedBy { (_, value) -> LocalDate.parse(value.bookingDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy")) }
    private var showHeader: Boolean = false
    private var currentBookingDate: String = ""

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int,
                 mapOfPassengerInformationToPrimaryKey:  Pair<Long, PassengerInformation>,
                 showHeader: Boolean,
                 editButtonClickListener: (Long, PassengerInformation) -> Unit,
                 removeButtonClickListener: (Int, Long) -> Unit) {

            if(showHeader) {
                itemView.headerContainer.visibility = View.VISIBLE
                itemView.textViewHeaderBookingDate.text = mapOfPassengerInformationToPrimaryKey.second.bookingDate
            }

            itemView.textViewPassengerName.text = mapOfPassengerInformationToPrimaryKey.second.name
            itemView.textViewContactNumber.text = mapOfPassengerInformationToPrimaryKey.second.number
            itemView.textViewOrigin.text = "Origin: " + mapOfPassengerInformationToPrimaryKey.second.origin
            itemView.textViewDestination.text = "Destination: " + mapOfPassengerInformationToPrimaryKey.second.destination
            itemView.editButton.setOnClickListener {
                if (mapOfPassengerInformationToPrimaryKey.second != null) {
                    if (mapOfPassengerInformationToPrimaryKey.first != null) {
                        editButtonClickListener(mapOfPassengerInformationToPrimaryKey.first, mapOfPassengerInformationToPrimaryKey.second)
                    }
                }
            }
            itemView.removeButton.setOnClickListener {
                if (mapOfPassengerInformationToPrimaryKey.second != null) {
                    if (mapOfPassengerInformationToPrimaryKey.first != null) {
                        removeButtonClickListener(position - 1, mapOfPassengerInformationToPrimaryKey.first)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booking_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var primaryKeys: MutableList<Long> = mutableListOf()
        mDataSet.forEach {
            primaryKeys.add(it.first)
        }

        if(currentBookingDate != mDataSet[position].second.bookingDate) {
            showHeader = true
            currentBookingDate = mDataSet[position].second.bookingDate
        }
        else {
            showHeader = false
        }
        holder.bind(position + 1, mDataSet[position], showHeader, editButtonClickListener, removeButtonClickListener)
    }

    fun deleteItemFromDataSet(myDataSet: Map<Long, PassengerInformation>) {
        mDataSet = myDataSet.toList()
        notifyDataSetChanged()
    }

}