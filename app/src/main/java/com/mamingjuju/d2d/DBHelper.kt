package com.mamingjuju.d2d

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

private const val SQL_CREATE_NEW_ENTRIES =
    "CREATE TABLE ${NewPassengerEntry.NewPassenger.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${NewPassengerEntry.NewPassenger.COLUMN_NAME} TEXT," +
            "${NewPassengerEntry.NewPassenger.COLUMN_NUMBER} TEXT," +
            "${NewPassengerEntry.NewPassenger.COLUMN_DATE} TEXT," +
            "${NewPassengerEntry.NewPassenger.COLUMN_ORIGIN} TEXT," +
            "${NewPassengerEntry.NewPassenger.COLUMN_DESTINATION} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${NewPassengerEntry.NewPassenger.TABLE_NAME}"

class DBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(SQL_CREATE_NEW_ENTRIES)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(p0)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "PassengerDB.db"
    }
}