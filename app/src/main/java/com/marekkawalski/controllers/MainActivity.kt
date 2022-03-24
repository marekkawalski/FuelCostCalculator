package com.marekkawalski.controllers

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

/**
 * Main activity
 * Class is apps main activity. On this screen, user can add cars that took part in a trip.
 * @author Marek Kawalski
 * @version 1.4
 */
class MainActivity : AppCompatActivity() {
    private var carMake: String? = null
    private var totalCost: Double? = null
    private var car: Car? = null
    private var dontKnowCheckBox: CheckBox? = null
    private var listOfCars = ArrayList<Car>()
    private var listOfCarsViews = ArrayList<TableRow>()
    private var tableCars: TableLayout? = null
    private var carNameInput: TextInputEditText? = null
    private var totalCostInput: TextInputEditText? = null
    private var bottomNavigationView: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = SettingsController()
        settings.loadSettings(this)

        setContentView(R.layout.activity_main)
        title = getString(R.string.app_full_name)

        bottomNavigationView = findViewById(R.id.bottom_navigationView)
        bottomNavigationView?.selectedItemId = R.id.Car

        bottomNavigationView?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        carNameInput = findViewById(R.id.carMakeInput)
        totalCostInput = findViewById(R.id.totalCostInput)
        dontKnowCheckBox = findViewById(R.id.dontKnowCostCheckBox)
        tableCars = findViewById(R.id.tableOfCars)

        val buttonAddCar: Button? = findViewById(R.id.buttonAddCar)

        buttonAddCar?.setOnClickListener {
            handleAddingNewCar()
        }

        val buttonDeleteCar: Button? = findViewById(R.id.buttonDeleteLastCar)
        buttonDeleteCar?.setOnClickListener {
            handleDeletingLastCar()
        }

        val buttonNext: ImageButton? = findViewById(R.id.buttonNext)
        buttonNext?.setOnClickListener {

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
            changeDontKnowCostVisibility()
        }
    }

    /**
     * Method handles adding new car.
     *
     */
    private fun handleAddingNewCar() {
        carMake = carNameInput?.text.toString()
        totalCost = totalCostInput?.text.toString()
            .toDoubleOrNull()

        if (carMake.isNullOrBlank()) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.provide_car_name),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        car = Car(carMake ?: resources.getString(R.string.no_data))
        if (dontKnowCheckBox?.isChecked == true) {
            if (!handleDontKnowCost()) return

        } else {
            if (totalCost == null) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.provide_total_cost),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return
            }
            if (totalCost ?: 0.0 <= 0.0) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.numberMustBePositive),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return
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

        tableCars?.addView(tableRow)
        listOfCarsViews.add(tableRow)

        //add car to list of cars
        listOfCars.add(car ?: return)

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
        carNameInput?.text?.clear() //clear input field
        totalCostInput?.text?.clear() //clear input field
    }

    /**
     * Method handles deleting last car.
     *
     */
    private fun handleDeletingLastCar() {
        if (listOfCars.isNotEmpty()) {
            listOfCars.removeLast()
        } else Toast.makeText(
            applicationContext,
            resources.getString(R.string.nothing_to_delete),
            Toast.LENGTH_SHORT
        )
            .show()

        if (listOfCarsViews.isNotEmpty()) {
            tableCars?.removeView(listOfCarsViews.last())
            listOfCarsViews.removeLast()
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.car_removed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Method handles situation when user does not know cost of fuel.
     * @return false if fuel per liter or average fuel consumption inputs are null of less equal to zero.
     */
    private fun handleDontKnowCost(): Boolean {
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
            return false
        } else if (pricePerLiterInput == null) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.provide_price_of_fuel_liter),
                Toast.LENGTH_SHORT
            ).show()
            return false
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
            return false
        }
        if (pricePerLiter == null) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.provide_correct_price_of_fuel_liter),
                Toast.LENGTH_SHORT
            )
                .show()
            return false
        }
        if (averageConsumption <= 0.0 || pricePerLiter <= 0.0) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.numberMustBePositive),
                Toast.LENGTH_SHORT
            )
                .show()
            return false
        }
        car?.costOfFuelLiter = pricePerLiter
        car?.averageFuelConsumptions = averageConsumption

        pricePerLiterInput.text?.clear()
        averageConsumptionInput.text?.clear()
        return true
    }

    /**
     * Method changes visibility of average fuel consumption and price per fuel liter input layouts.
     */
    private fun changeDontKnowCostVisibility() {
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


