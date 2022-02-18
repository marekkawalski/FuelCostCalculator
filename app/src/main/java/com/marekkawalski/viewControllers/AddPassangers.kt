package com.marekkawalski.viewControllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.marekkawalski.fuelcostcalculator.R

class AddPassangers : AppCompatActivity() {
    private var passangers: ArrayList<String>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_passangers)
    }
}