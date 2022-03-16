package com.marekkawalski.viewControllers

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.marekkawalski.fuelcostcalculator.R
import model.Car
import otherControllers.SettingsController

/**
 * Main activity
 * Class is apps main activity. On this screen, user can add cars that took part in a trip
 * @author Marek Kawalski
 */
class MainActivity : AppCompatActivity() {
    private var carMake: String? = null
    private var totalCost: Double? = null
    private var car: Car? = null
    private var dontKnowCheckBox: CheckBox? = null
    private var listOfCars = ArrayList<Car>()
    private var listOfCarsViews = ArrayList<TableRow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = SettingsController()
        settings.loadSettings(this, resources)

        setContentView(R.layout.activity_main)
        title = getString(R.string.app_full_name)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigationView)
        bottomNavigationView.selectedItemId = R.id.Car

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        val carNameInput = findViewById<TextInputEditText>(R.id.carMakeInput)
        val totalCostInput = findViewById<TextInputEditText>(R.id.totalCostInput)

        dontKnowCheckBox = findViewById(R.id.dontKnowCostCheckBox)
        val tableCars = findViewById<TableLayout>(R.id.tableOfCars)

        val buttonAddCar = findViewById<Button>(R.id.buttonAddCar)

        buttonAddCar.setOnClickListener OnClickListener@{

            carMake = carNameInput.text.toString()
            totalCost = totalCostInput.text.toString()
                .toDoubleOrNull()

            if (carMake.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.provide_car_name),
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            car = Car(carMake ?: resources.getString(R.string.no_data))
            if (dontKnowCheckBox?.isChecked == true) {
                val averageConsumptionInput =
                    findViewById<TextInputEditText>(R.id.averageFuelConsumptionInput)
                val pricePerLiterInput =
                    findViewById<TextInputEditText>(R.id.pricePerLiterInput)
                if (averageConsumptionInput == null) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.provide_average_consumption),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                } else if (pricePerLiterInput == null) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.provide_price_of_fuel_liter),
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
                        resources.getString(R.string.provide_correct_fuel_consumption),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@OnClickListener
                }
                if (pricePerLiter == null) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.provide_correct_price_of_fuel_liter),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@OnClickListener
                }
                car?.costOfFuelLiter = pricePerLiter
                car?.averageFuelConsumptions = averageConsumption

                pricePerLiterInput.text?.clear()
                averageConsumptionInput.text?.clear()

            } else {
                if (totalCost == null) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.provide_total_cost),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@OnClickListener
                }
                car?.totalFuelCost = totalCost ?: 0.0
            }

            //create new table rows as user adds more cars
            val carNameView = TextView(this)
            val carFuelView = TextView(this)
            val tableRow: TableRow?
            carNameView.text = car?.carName
            carNameView.gravity = Gravity.CENTER
            carNameView.width = ViewGroup.LayoutParams.WRAP_CONTENT

            if (car?.totalFuelCost == 0.0) {
                carFuelView.text = resources.getString(R.string.notSetYet)
            } else {
                carFuelView.text = car?.totalFuelCost.toString()
            }
            carFuelView.gravity = Gravity.CENTER
            carFuelView.width = ViewGroup.LayoutParams.WRAP_CONTENT

            tableRow = TableRow(this)
            tableRow.addView(carNameView)
            tableRow.addView(carFuelView)

            tableCars.addView(tableRow)
            listOfCarsViews.add(tableRow)

            //add car to list of cars
            listOfCars.add(car ?: return@OnClickListener)

            //if everything goes smoothly
            Toast.makeText(
                applicationContext,
                "${resources.getString(R.string.car)} \"${carNameView.text}\" ${
                    resources.getString(
                        R.string.added
                    )
                }",
                Toast.LENGTH_SHORT
            ).show()
            carNameInput.text?.clear() //clear input field
            totalCostInput.text?.clear() //clear input field

        }

        val buttonDeleteCar = findViewById<Button>(R.id.buttonDeleteLastCar)
        buttonDeleteCar.setOnClickListener {
            if (listOfCars.isNotEmpty()) {
                listOfCars.removeLast()
            } else Toast.makeText(
                applicationContext,
                resources.getString(R.string.nothing_to_delete),
                Toast.LENGTH_SHORT
            )
                .show()

            if (listOfCarsViews.isNotEmpty()) {
                tableCars.removeView(listOfCarsViews.last())
                listOfCarsViews.removeLast()
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.car_removed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val buttonNext = findViewById<ImageButton>(R.id.buttonNext)
        buttonNext.setOnClickListener {

            if (listOfCars.isNotEmpty()) {
                //move data to next activity
                val intent = Intent(this, AddDistances::class.java)
                intent.putExtra("listOfCars", listOfCars)
                startActivity(intent)
            } else {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.addAtLeastOneCar),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dontKnowCheckBox?.setOnCheckedChangeListener { _, _ ->
            handleDontKnowCost()
        }
    }

    /**
     * Handle dont know cost
     * Method changes visibility of average fuel consumption and price per fuel liter input layouts
     */
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


