package com.mamingjuju.d2d

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class BookingListAdapter(
    myDataSet: Map<Long, PassengerInformation>,
    private val editButtonClickListener: (Long, PassengerInformation) -> Unit,
    private val cardViewClickListener: (Long) -> Unit,
    private val onCallButtonClicked: (String) -> Unit,
    private val onEmptyListImageClicked: () -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var mDataSet = myDataSet.toList().sortedBy { (_, value) -> LocalDate.parse(value.bookingDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy")) }
    private var mDataSetFull = myDataSet.toList().sortedBy { (_, value) -> LocalDate.parse(value.bookingDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy")) }
    private var showHeader: Boolean = false
    private var currentBookingDate: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.empty_list_layout -> EmptyListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_list_layout, parent, false))
            else -> PassengerInfoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.booking_detail, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if(mDataSet.isEmpty()) {
            1
        }
        else {
            mDataSet.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (mDataSet.size) {
            0 -> R.layout.empty_list_layout
            else -> R.layout.booking_detail
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is PassengerInfoViewHolder) {
            val primaryKeys: MutableList<Long> = mutableListOf()
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

            holder.bind(mDataSet[position], showHeader,
                editButtonClickListener, cardViewClickListener, onCallButtonClicked)
        }
        else if(holder is EmptyListViewHolder){
            if(mDataSetFull.isNotEmpty()) {
                holder.bind("No matching passenger on your list", onEmptyListImageClicked, false)
            } else {
                holder.bind("You have no passenger listed yet", onEmptyListImageClicked, true)
            }
        }
    }

    override fun getFilter(): Filter {
        return dataSetFilter
    }

    fun deleteItemFromDataSet(myDataSet: Map<Long, PassengerInformation>) {
        mDataSet     = myDataSet.toList().sortedBy { (_, value) -> LocalDate.parse(value.bookingDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy")) }
        mDataSetFull = myDataSet.toList().sortedBy { (_, value) -> LocalDate.parse(value.bookingDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy")) }
        currentBookingDate = ""
        notifyDataSetChanged()
    }

    private val dataSetFilter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredDataSet: MutableList<Pair<Long, PassengerInformation>> = mutableListOf()
            if (constraint.isEmpty()) {
                filteredDataSet.addAll(mDataSetFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                for(data in mDataSet) {
                    if(data.second.name.toLowerCase(Locale.ROOT).contains(filterPattern)) filteredDataSet.add(data)
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
