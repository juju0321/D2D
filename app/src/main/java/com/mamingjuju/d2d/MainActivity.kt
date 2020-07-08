package com.mamingjuju.d2d

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bookingListFragment = BookingListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainViewFragment, bookingListFragment)
            .commit()
    }
}