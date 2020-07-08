package com.mamingjuju.d2d

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.mamingjuju.d2d.databinding.NewBookingFragmentBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("DEPRECATION")
class NewBookingFragment : Fragment() {

    private var mDbHelper: DBHelper? = null
    private var mIsToUpdateDb = false
    private var mPrimaryKeyToUpdate: Long? = 999999999
    private var mPassengerInformation: PassengerInformation? = null

    private var mPassengerNameEditText: EditText? = null
    private var mPassengerNumberEditText: EditText? = null
    private var mPassengerBookingDateEditText: EditText? = null
    private var mPassengerOriginEditText: EditText? = null
    private var mPassengerDestinationEditText: EditText? = null

    private var _binding: NewBookingFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDbHelper = context?.let { DBHelper(it) }
        val bundle = this.arguments

        if(bundle != null) {
            mIsToUpdateDb = true
            mPassengerInformation = bundle.getParcelable("passengerInfo")
            mPrimaryKeyToUpdate = bundle.getLong("primaryKey")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NewBookingFragmentBinding.inflate(inflater, container, false)
        val rootView = binding.root
        initViews()

        if(mPassengerInformation != null) {
            mPassengerNameEditText?.setText(mPassengerInformation!!.name)
            mPassengerNumberEditText?.setText(mPassengerInformation!!.number)
            mPassengerBookingDateEditText?.setText(mPassengerInformation!!.bookingDate)
            mPassengerOriginEditText?.setText(mPassengerInformation!!.origin)
            mPassengerDestinationEditText?.setText(mPassengerInformation!!.destination)
            (activity as MainActivity).setActionBarTitle("Edit Passenger Information")
        }
        else {
            (activity as MainActivity).setActionBarTitle("Add Passenger Information")
        }

        binding.textFieldBookingDate.setEndIconOnClickListener {
            showDateTimePicker(mPassengerBookingDateEditText)
        }

        binding.textFieldBookingDate.editText?.inputType = InputType.TYPE_NULL
        binding.textFieldBookingDate.editText?.setOnClickListener {
            showDateTimePicker(it as EditText)
        }
        binding.textFieldBookingDate.editText?.setOnFocusChangeListener { view, _ ->
            if(view.hasFocus()) {
                activity?.let { hideKeyboard(it) }
                showDateTimePicker(view as EditText)
            }
        }
        binding.buttonSave.setOnClickListener {
            onClickSave()
            mDbHelper?.close()
            fragmentManager?.popBackStackImmediate()
            startBookingListFragment()
        }

        binding.buttonCancel.setOnClickListener {
            mDbHelper?.close()
            fragmentManager?.popBackStackImmediate()
            startBookingListFragment()
        }

        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mDbHelper?.close()
    }

    private fun initViews() {
        mPassengerNameEditText        = binding.textFieldName.editText
        mPassengerNumberEditText      = binding.textFieldContactNumber.editText
        mPassengerBookingDateEditText = binding.textFieldBookingDate.editText
        mPassengerOriginEditText      = binding.textFieldOrigin.editText
        mPassengerDestinationEditText = binding.textFieldDestination.editText
    }

    private fun onClickSave() {
        val passengerName        = mPassengerNameEditText?.text
        val passengerNumber      = mPassengerNumberEditText?.text
        val passengerBookingDate = mPassengerBookingDateEditText?.text
        val passengerOrigin      = mPassengerOriginEditText?.text
        val passengerDestination = mPassengerDestinationEditText?.text

        val passengerInformation = PassengerInformation(passengerName.toString(),
            passengerNumber.toString(),
            passengerBookingDate.toString(),
            passengerOrigin.toString(),
            passengerDestination.toString())

        if(mIsToUpdateDb) {
            updatePassengerInformationViaPrimaryKey(passengerInformation)
        } else {
            addNewBooking(passengerInformation)
        }

        passengerName?.clear()
        passengerNumber?.clear()
        passengerBookingDate?.clear()
        passengerOrigin?.clear()
        passengerDestination?.clear()
    }

    private fun addNewBooking(passengerInformation: PassengerInformation): Boolean {
        val dbWrite = mDbHelper?.writableDatabase
        val newPassengerInformation = ContentValues().apply {
            put(NewPassengerEntry.NewPassenger.COLUMN_NAME, passengerInformation.name)
            put(NewPassengerEntry.NewPassenger.COLUMN_NUMBER, passengerInformation.number)
            put(NewPassengerEntry.NewPassenger.COLUMN_DATE, passengerInformation.bookingDate)
            put(NewPassengerEntry.NewPassenger.COLUMN_ORIGIN, passengerInformation.origin)
            put(NewPassengerEntry.NewPassenger.COLUMN_DESTINATION, passengerInformation.destination)
        }
        val rowInserted = dbWrite?.insert(NewPassengerEntry.NewPassenger.TABLE_NAME, null, newPassengerInformation)
        return rowInserted != (-1).toLong()
    }

    private fun updatePassengerInformationViaPrimaryKey(passengerInformation: PassengerInformation) {
        val dbWrite = mDbHelper?.writableDatabase
        val updatedPassengerInformation = ContentValues().apply {
            put(NewPassengerEntry.NewPassenger.COLUMN_NAME, passengerInformation.name)
            put(NewPassengerEntry.NewPassenger.COLUMN_NUMBER, passengerInformation.number)
            put(NewPassengerEntry.NewPassenger.COLUMN_DATE, passengerInformation.bookingDate)
            put(NewPassengerEntry.NewPassenger.COLUMN_ORIGIN, passengerInformation.origin)
            put(NewPassengerEntry.NewPassenger.COLUMN_DESTINATION, passengerInformation.destination)
        }
        dbWrite?.update(NewPassengerEntry.NewPassenger.TABLE_NAME,
        updatedPassengerInformation,
            "_id = $mPrimaryKeyToUpdate",
            null
        )
    }

    private fun startBookingListFragment() {
        val bookingListFragment = BookingListFragment()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.mainViewFragment, bookingListFragment)
            ?.commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDateTimePicker(editText: EditText?) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()

        picker.addOnCancelListener{
            Log.i("${NewBookingFragment::class.simpleName}", "Dialog was cancelled")
            editText?.isFocusable = true
        }

        picker.addOnNegativeButtonClickListener {
            Log.i("${NewBookingFragment::class.simpleName}", "Dialog negative button was clicked")
        }

        picker.addOnPositiveButtonClickListener {
            val date = SimpleDateFormat("EEE, MMMM dd, yyyy").format(Date(it))
            Log.i("Date picker", "From picker: $date")
            editText?.setText(date)
            val stringDate = editText?.text.toString()
            Log.i("Date picker", "String: $date")
            val parsedDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("EEE, MMMM dd, yyyy"))
            Log.i("Date picker", "Parsed date: $date")
        }

        fragmentManager?.let { picker.show(it, picker.toString()) }
    }

    fun hideKeyboard(activity: Activity) {
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}