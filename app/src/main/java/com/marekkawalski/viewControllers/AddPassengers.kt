package com.marekkawalski.viewControllers

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.marekkawalski.fuelcostcalculator.R
import model.Car
import model.Distance


class AddPassengers : AppCompatActivity() {
    private var passengerName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_passangers)
        title = "Fuel cost calculator"
        val car = intent.getParcelableExtra<Car>("car")
        val distancesList = intent.getParcelableArrayListExtra<Distance>("listOfDistances")

        val addPassengerButton = findViewById<Button>(R.id.addPassengerButton)
        val deleteLastPassengerButton = findViewById<Button>(R.id.deleteLastButton)
        val nextScreenButton = findViewById<ImageButton>(R.id.buttonNextResults)
        val tableOfPassengers = findViewById<TableLayout>(R.id.tableLayout)
        val passengerNameInput = findViewById<TextInputEditText>(R.id.passengerNameInput)

        addPassengerButton.setOnClickListener {
            passengerName = passengerNameInput.text.toString()
            if (passengerName.isNullOrBlank()) {
                Toast.makeText(applicationContext, "Provide passenger name!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val passengerNameView = TextView(this)
            val distancesTextView = TextView(this)
            val tableRow = TableRow(this)
            passengerNameView.text = passengerName
            passengerNameView.gravity = Gravity.CENTER




            tableRow.addView(passengerNameView)
            tableRow.addView(distancesTextView)
            tableOfPassengers.addView(tableRow)
        }

    }

}