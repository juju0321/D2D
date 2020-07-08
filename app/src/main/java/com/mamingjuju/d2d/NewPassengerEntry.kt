package com.mamingjuju.d2d

import android.provider.BaseColumns

object NewPassengerEntry {
    object NewPassenger: BaseColumns {
        const val TABLE_NAME = "passenger"
        const val COLUMN_NAME = "name"
        const val COLUMN_NUMBER = "number"
        const val COLUMN_DATE = "date"
        const val COLUMN_ORIGIN = "origin"
        const val COLUMN_DESTINATION = "destination"
    }
}