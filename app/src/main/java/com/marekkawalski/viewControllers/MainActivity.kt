package com.marekkawalski.viewControllers

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
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
            if (carMake.isNullOrBlank()) {
                Toast.makeText(applicationContext, "Provide car name!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (totalDistance == null) {
                Toast.makeText(applicationContext, "Provide total distance", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            val car = Car(carMake ?: "no data", totalDistance ?: 0.0)
            if (dontKnowCheckBox.isChecked) {
                ///@toDo handle not known cost of fuel
            } else {
                if (totalCost == null) {
                    Toast.makeText(applicationContext, "Provide total cost!", Toast.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }
                car.totalFuelCost = totalCost ?: 0.00
            }
            val test = findViewById<TextView>(R.id.textView3)
            test.text = "" + car.avarageFuelConsumptions + "\t" + car.totalDistance + "\t"+ car.totalFuelCost
        })
    }

}