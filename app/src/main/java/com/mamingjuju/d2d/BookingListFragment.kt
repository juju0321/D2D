package com.mamingjuju.d2d

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BookingListFragment : Fragment() {

    private var mDbHelper: DBHelper? = null
    private var mDataSet: MutableMap<Long, PassengerInformation>? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mViewAdapter: BookingListAdapter
    private lateinit var mViewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDbHelper = context?.let { DBHelper(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_booking_list, container, false)
        (activity as MainActivity).setActionBarTitle("Passenger List")
        mDataSet     = readDB()
        mViewManager = LinearLayoutManager(context)
        mViewAdapter = BookingListAdapter(
            mDataSet!!,
            { primaryKey: Long, passengerInformation: PassengerInformation? -> cardViewClicked(primaryKey, passengerInformation) },
            { position: Int, primaryKey: Long -> removeButtonClicked(position, primaryKey)},
            { uriToParse: String -> onCallButtonClicked(uriToParse)})
        mRecyclerView = rootView.findViewById(R.id.bookingListRecyclerView)
        mRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mViewManager
            adapter       = mViewAdapter
        }

        val newBookingFragment = NewBookingFragment()
        val fabAddNewBooking   = rootView.findViewById<FloatingActionButton>(R.id.fabAddNewBooking)
        fabAddNewBooking.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.mainViewFragment, newBookingFragment)
                ?.addToBackStack("fragmentList")
                ?.commit()
        }

        return rootView
    }

    private fun cardViewClicked(primaryKey: Long, passengerInformation: PassengerInformation?) {
        val newBookingFragment = NewBookingFragment()
        val bundle = Bundle()
        bundle.putParcelable("passengerInfo", passengerInformation)
        bundle.putLong("primaryKey", primaryKey)
        newBookingFragment.arguments = bundle

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.mainViewFragment, newBookingFragment)
            ?.addToBackStack("fragmentList")
            ?.commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun removeButtonClicked(position: Int, primaryKey: Long) {
        val dbWrite = mDbHelper?.writableDatabase
        dbWrite?.delete(NewPassengerEntry.NewPassenger.TABLE_NAME, "_id = $primaryKey", null)
        mDataSet = readDB()
        mViewAdapter.deleteItemFromDataSet(mDataSet!!)
        mViewAdapter.notifyDataSetChanged()
    }

    private fun onCallButtonClicked(uriToParse: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        var checkCallPermission: Int = -1
        callIntent.data = Uri.parse(uriToParse)
        if(context != null) {
            checkCallPermission = ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE)
        }
        if(checkCallPermission != PackageManager.PERMISSION_GRANTED) {
            activity?.let { ActivityCompat.requestPermissions(it,
                arrayOf(android.Manifest.permission.CALL_PHONE), 1
            ) }
        }

        startActivity(callIntent)
    }

    private fun readDB(): MutableMap<Long, PassengerInformation> {
        var passengerInfoToPrimaryKeysMap : MutableMap<Long, PassengerInformation> = mutableMapOf()
        val passengerInformationList = mutableListOf<Map<Long, PassengerInformation>>()
        val dbReader   = mDbHelper?.readableDatabase
        val projection = arrayOf(BaseColumns._ID, NewPassengerEntry.NewPassenger.COLUMN_NAME,
            NewPassengerEntry.NewPassenger.COLUMN_NUMBER, NewPassengerEntry.NewPassenger.COLUMN_DATE,
            NewPassengerEntry.NewPassenger.COLUMN_ORIGIN, NewPassengerEntry.NewPassenger.COLUMN_DESTINATION)

        val cursor = dbReader?.query(NewPassengerEntry.NewPassenger.TABLE_NAME, projection,
            null, null, null, null, null)

        if(cursor != null) {
            with(cursor) {
                while (moveToNext()) {
                    val id          = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val name        = getString(cursor.getColumnIndex("name"))
                    val number      = cursor.getString(cursor.getColumnIndex("number"))
                    val bookingDate = cursor.getString(cursor.getColumnIndex("date"))
                    val origin      = cursor.getString(cursor.getColumnIndex("origin"))
                    val destination = cursor.getString(cursor.getColumnIndex("destination"))

                    passengerInfoToPrimaryKeysMap[id] = PassengerInformation(name, number, bookingDate, origin, destination)
                    passengerInformationList.add(passengerInfoToPrimaryKeysMap)
                }
            }
        }
        return passengerInfoToPrimaryKeysMap
    }

}