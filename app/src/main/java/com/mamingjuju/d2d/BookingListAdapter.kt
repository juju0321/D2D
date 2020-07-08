package com.mamingjuju.d2d

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.booking_detail.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class BookingListAdapter(myDataSet: Map<Long, PassengerInformation>,
                         private val editButtonClickListener: (Long, PassengerInformation) -> Unit,
                         private val cardViewClickListener: (Int, Long) -> Unit,
                         private val onCallButtonClicked: (String) -> Unit): RecyclerView.Adapter<BookingListAdapter.ViewHolder>(), Filterable {

    private var mDataSet = myDataSet.toList().sortedBy { (_, value) -> LocalDate.parse(value.bookingDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy")) }
    private var mDataSetFull = myDataSet.toList().sortedBy { (_, value) -> LocalDate.parse(value.bookingDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy")) }
    private var showHeader: Boolean = false
    private var currentBookingDate: String = ""

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int,
                 mapOfPassengerInformationToPrimaryKey:  Pair<Long, PassengerInformation>,
                 showHeader: Boolean,
                 cardViewClickListener: (Long, PassengerInformation) -> Unit,
                 removeButtonClickListener: (Int, Long) -> Unit,
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
                if (mapOfPassengerInformationToPrimaryKey.second != null) {
                    if (mapOfPassengerInformationToPrimaryKey.first != null) {
                        onCallButtonClicked("tel:${mapOfPassengerInformationToPrimaryKey.second.number}")
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
            itemView.setOnClickListener {
                if (mapOfPassengerInformationToPrimaryKey.second != null) {
                    if (mapOfPassengerInformationToPrimaryKey.first != null) {
                        cardViewClickListener(mapOfPassengerInformationToPrimaryKey.first, mapOfPassengerInformationToPrimaryKey.second)
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

        holder.bind(position + 1, mDataSet[position], showHeader,
            editButtonClickListener, cardViewClickListener, onCallButtonClicked)
    }

    fun deleteItemFromDataSet(myDataSet: Map<Long, PassengerInformation>) {
        mDataSet     = myDataSet.toList().sortedBy { (_, value) -> LocalDate.parse(value.bookingDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy")) }
        mDataSetFull = myDataSet.toList().sortedBy { (_, value) -> LocalDate.parse(value.bookingDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy")) }
        currentBookingDate = ""
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return dataSetFilter
    }

    private val dataSetFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredDataSet: MutableList<Pair<Long, PassengerInformation>> = mutableListOf()
            if (constraint.isNullOrEmpty()) {
                filteredDataSet.addAll(mDataSetFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()
                for(data in mDataSet) {
                    if(data.second.name.toLowerCase().contains(filterPattern)) filteredDataSet.add(data)
                }
            }
            return FilterResults().apply {
                values = filteredDataSet
            }
        }
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            mDataSet = results.values as List<Pair<Long, PassengerInformation>>
            currentBookingDate = ""
            notifyDataSetChanged()
        }
    }
}
