package com.mamingjuju.d2d

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PassengerInformation(
    val name: String,
    val number: String,
    val bookingDate: String,
    val origin: String,
    val destination: String
) : Parcelable