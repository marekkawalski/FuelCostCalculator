package com.marekkawalski.viewControllers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.marekkawalski.fuelcostcalculator.R
import model.Car

class MainActivity : AppCompatActivity() {
    private var carMake: String? = null
    private var totalCost: Double? = null
    private var car: Car? = null
    private var dontKnowCheckBox: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_full_name)
        val buttonNext = findViewById<ImageButton>(R.id.buttonNext)
        dontKnowCheckBox = findViewById(R.id.dontKnowCostCheckBox)

        buttonNext.setOnClickListener(View.OnClickListener {
            carMake = findViewById<TextInputEditText>(R.id.carMakeInput).text.toString()
            totalCost = findViewById<TextInputEditText>(R.id.totalCostInput).text.toString()
                .toDoubleOrNull()

            if (carMake.isNullOrBlank()) {
                Toast.makeText(applicationContext, "Provide car name!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            car = Car(carMake ?: "no data")
            if (dontKnowCheckBox?.isChecked == true) {
                val averageConsumptionInput =
                    findViewById<TextInputEditText>(R.id.averageFuelConsumptionInput)
                val pricePerLiterInput =
                    findViewById<TextInputEditText>(R.id.pricePerLiterInput)
                if (averageConsumptionInput == null) {
                    Toast.makeText(
                        applicationContext,
                        "Provide average consumption!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                } else if (pricePerLiterInput == null) {
                    Toast.makeText(
                        applicationContext,
                        "Provide price per liter!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }

                val averageConsumption = averageConsumptionInput.text.toString()
                    .toDoubleOrNull()
                val pricePerLiter = pricePerLiterInput.text.toString().toDoubleOrNull()

                if (averageConsumption == null) {
                    Toast.makeText(
                        applicationContext,
                        "Provide correct average consumption!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@OnClickListener
                }
                if (pricePerLiter == null) {
                    Toast.makeText(
                        applicationContext,
                        "Provide correct price of fuel liter!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@OnClickListener
                }
                car?.costOfFuelLiter = pricePerLiter
                car?.averageFuelConsumptions = averageConsumption
            } else {
                if (totalCost == null) {
                    Toast.makeText(applicationContext, "Provide total cost!", Toast.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }
                car?.totalFuelCost = totalCost ?: 0.0
            }

            //move data to next activity
            val intent = Intent(this, AddDistances::class.java)
            intent.putExtra("car", car)
            startActivity(intent)
        })

        dontKnowCheckBox?.setOnCheckedChangeListener { _, _ ->
            handleDontKnowCost()
        }
    }

    operator fun Double?.plus(other: Double?): Double? =
        if (this != null && other != null) this + other else null

    private fun handleDontKnowCost() {
        val averageConsumptionLayout =
            findViewById<TextInputLayout>(R.id.averageConsumptionInputLayout)
        val pricePerLiterLayout = findViewById<TextInputLayout>(R.id.pricePerLiterInputLayout)
        if (dontKnowCheckBox?.isChecked == true) {
            averageConsumptionLayout.visibility = View.VISIBLE
            pricePerLiterLayout.visibility = View.VISIBLE
        } else {
            averageConsumptionLayout.visibility = View.GONE
            pricePerLiterLayout.visibility = View.GONE
        }
    }
}


