package com.marekkawalski.viewControllers

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.marekkawalski.fuelcostcalculator.R
import model.Car

class MainActivity : AppCompatActivity() {
    private var carMake: String? = null
    private var totalDistance: Double? = null
    private var totalCost: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonNext = findViewById<Button>(R.id.buttonNext)
        val dontKnowCheckBox = findViewById<CheckBox>(R.id.dontKnowCostCheckBox)

        buttonNext.setOnClickListener(View.OnClickListener {
            carMake = findViewById<TextInputEditText>(R.id.carMakeInput).text.toString()
            totalDistance = findViewById<TextInputEditText>(R.id.totalDistanceInput).text.toString()
                .toDoubleOrNull()
            totalCost = findViewById<TextInputEditText>(R.id.totalCostInput).text.toString()
                .toDoubleOrNull()
            val car = Car(carMake ?: "no data", totalDistance ?: 0.0)
            if (dontKnowCheckBox.isChecked) {
                ///@toDo handle not known cost of fuel
            } else {
                car.totalFuelCost = totalCost ?: 0.00
            }
            val test = findViewById<TextView>(R.id.textView3)
            test.text = "" + car.avarageFuelConsumptions + "\t" + car.totalDistance + "\t"+ car.totalFuelCost
        })
    }


}