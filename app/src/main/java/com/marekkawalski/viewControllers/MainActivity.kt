package com.marekkawalski.viewControllers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.marekkawalski.fuelcostcalculator.R
import model.Car

class MainActivity : AppCompatActivity() {
    private var carMake: String? = null
    private var totalCost: Double? = null
    private lateinit var car: Car
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Fuel cost calculator"
        val buttonNext = findViewById<Button>(R.id.buttonNext)
        val dontKnowCheckBox = findViewById<CheckBox>(R.id.dontKnowCostCheckBox)

        buttonNext.setOnClickListener(View.OnClickListener {
            carMake = findViewById<TextInputEditText>(R.id.carMakeInput).text.toString()
            totalCost = findViewById<TextInputEditText>(R.id.totalCostInput).text.toString()
                .toDoubleOrNull()
            if (carMake.isNullOrBlank()) {
                Toast.makeText(applicationContext, "Provide car name!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            car = Car(carMake ?: "no data")
            if (dontKnowCheckBox.isChecked) {
                ///@toDo handle not known cost of fuel
            } else {
                if (totalCost == null) {
                    Toast.makeText(applicationContext, "Provide total cost!", Toast.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }
                car.totalFuelCost = totalCost ?: 0.00

                //move to activity where calculations are made
                val intent = Intent(this, AddDistances::class.java)
                intent.putExtra("car", car)
                startActivity(intent)
            }
        })
    }

}


