package com.marekkawalski.viewControllers

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.marekkawalski.fuelcostcalculator.R
import model.Person

class Results : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_results)
        title = "Fuel cost calculator"

        val listOfPassengers = intent.getParcelableArrayListExtra<Person>("listOfPassengers")
        val resulsTextView = findViewById<TextView>(R.id.resultsTest)
        if (listOfPassengers != null) {
            if (listOfPassengers.isEmpty()) {
                resulsTextView.text = "list is empty"
            } else {
                resulsTextView.text = listOfPassengers[0]?.name ?: "null niestety"
            }
        } else {
            resulsTextView.text = "list is null"
        }

    }

}