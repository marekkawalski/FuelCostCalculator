package com.marekkawalski.viewControllers

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.marekkawalski.fuelcostcalculator.R
import model.Person
import kotlin.math.roundToInt

class Results : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_results)
        title = "Fuel cost calculator"

        val listOfPassengers = intent.getParcelableArrayListExtra<Person>("listOfPassengers")
        val tableOfPassengers = findViewById<TableLayout>(R.id.tableOfCostsLayout)

        if (listOfPassengers != null) {
            for (i in listOfPassengers) {
                val passengerNameView = TextView(this)
                val costTextView = TextView(this)
                val tableRow = TableRow(this)

                passengerNameView.text = i.name
                costTextView.text = ((i.costOfFuel * 100.0).roundToInt() / 100.0).toString()

                passengerNameView.gravity = Gravity.CENTER
                passengerNameView.width = WRAP_CONTENT

                costTextView.gravity = Gravity.CENTER
                costTextView.width = WRAP_CONTENT

                tableRow.addView(passengerNameView)
                tableRow.addView(costTextView)
                tableOfPassengers.addView(tableRow)
            }
        }

    }
}