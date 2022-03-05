package com.marekkawalski.viewControllers

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.marekkawalski.fuelcostcalculator.R
import model.Car
import model.Distance
import model.FuelCostCalculator
import model.Person


class AddPassengers : AppCompatActivity() {

    private var passengerName: String? = null
    private var listOfPassengers = ArrayList<Person>()
    private var choiceList = ArrayList<String>()
    private var selectedChoiceList = ArrayList<Boolean>()
    private var listOfPassengersSelectedChoice = ArrayList<BooleanArray>()
    private var listOfPassengersViews = ArrayList<TableRow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_passangers)
        title = getString(R.string.app_full_name)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigationView)
        bottomNavigationView.selectedItemId = R.id.Car


        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Car -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.settings -> {
                    val intent = Intent(this, Settings::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        var listOfPassengersIndex = -1
        val car = intent.getParcelableExtra<Car>("car")
        val distancesList = intent.getParcelableArrayListExtra<Distance>("listOfDistances")
        val addPassengerButton = findViewById<Button>(R.id.addPassengerButton)
        val deleteLastPassengerButton = findViewById<Button>(R.id.deleteLastButton)
        val nextScreenButton = findViewById<ImageButton>(R.id.buttonNextResults)
        val tableOfPassengers = findViewById<TableLayout>(R.id.tableOfCostsLayout)
        val passengerNameInput = findViewById<TextInputEditText>(R.id.passengerNameInput)
        val passengersTextView = findViewById<TextView>(R.id.passengersTextView)
        val buttonPrevious = findViewById<ImageButton>(R.id.buttonPrevious)
        var selectedDistancesString = ""

        passengersTextView.text =
            " ${resources.getString(R.string.passengers)} \n" + car?.carName
        if (distancesList != null) {
            for (i in distancesList) {
                choiceList.add(i.distanceName)
                selectedChoiceList.add(true)
            }
        }
        val choiceArray = choiceList.toTypedArray()

        addPassengerButton.setOnClickListener {
            passengerName = passengerNameInput.text.toString()
            if (passengerName.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.provide_passenger_name),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            val person = Person(passengerName as String, ++listOfPassengersIndex)
            listOfPassengers.add(person)

            val passengerNameView = TextView(this)
            val distancesTextView = Button(this)
            val tableRow = TableRow(this)
            passengerNameView.text = passengerName
            passengerNameView.gravity = Gravity.CENTER
            passengerNameView.width = WRAP_CONTENT

            distancesTextView.gravity = Gravity.CENTER
            distancesTextView.width = WRAP_CONTENT

            listOfPassengersViews.add(tableRow)

            val selectedChoiceArray = selectedChoiceList.toBooleanArray()
            listOfPassengersSelectedChoice.add(selectedChoiceArray)

            if (distancesList != null) {
                for (i in distancesList) {
                    person.listOfPassengersSelectedDistances.add(i)
                    i.passengersCount++
                }
            }
            for (i in person.listOfPassengersSelectedDistances) {
                selectedDistancesString += if (i != person.listOfPassengersSelectedDistances.last()) {
                    i.distanceName + ", "
                } else {
                    i.distanceName + " "
                }
            }
            distancesTextView.text = selectedDistancesString.ifEmpty { "nothing" }

            selectedDistancesString = ""

            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("${resources.getString(R.string.choose_distances_for)} $passengerName")
            alertDialog.setMultiChoiceItems(
                choiceArray,
                selectedChoiceArray
            ) { _: DialogInterface, position: Int, check: Boolean ->
                listOfPassengersSelectedChoice.last()[position] = check

                if (check) {
                    person.listOfPassengersSelectedDistances.add(
                        distancesList?.get(
                            position
                        ) ?: return@setMultiChoiceItems
                    )
                    distancesList[position].passengersCount++

                } else {
                    person.listOfPassengersSelectedDistances.remove(
                        distancesList?.get(
                            position
                        ) ?: return@setMultiChoiceItems
                    )
                    distancesList[position].passengersCount--
                }
                for (i in person.listOfPassengersSelectedDistances) {
                    selectedDistancesString += if (i != person.listOfPassengersSelectedDistances.last()) {
                        i.distanceName + ", "
                    } else {
                        i.distanceName + ""
                    }
                }
                distancesTextView.text = selectedDistancesString.ifEmpty { "nothing" }
                selectedDistancesString = " "
            }
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Ok") { _, _ ->
            }
            alertDialog.show()

            tableRow.addView(passengerNameView)
            tableRow.addView(distancesTextView)
            tableOfPassengers.addView(tableRow)

            distancesTextView.setOnClickListener {
                alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle(
                    resources.getString(R.string.choose_distances_for) + listOfPassengers[person.id].name
                )
                alertDialog.setMultiChoiceItems(
                    choiceArray,
                    listOfPassengersSelectedChoice[person.id]
                ) { _: DialogInterface, position: Int, check: Boolean ->

                    if (check) {
                        listOfPassengersSelectedChoice[person.id][position] = true
                        listOfPassengers[person.id].listOfPassengersSelectedDistances.add(
                            distancesList?.get(
                                position
                            ) ?: return@setMultiChoiceItems

                        )
                        distancesList[position].passengersCount++

                    } else {
                        listOfPassengersSelectedChoice[person.id][position] = false
                        listOfPassengers[person.id].listOfPassengersSelectedDistances.remove(
                            distancesList?.get(
                                position
                            ) ?: return@setMultiChoiceItems
                        )
                        distancesList[position].passengersCount--

                    }
                    for (i in person.listOfPassengersSelectedDistances) {
                        selectedDistancesString += if (i != person.listOfPassengersSelectedDistances.last()) {
                            i.distanceName + ", "
                        } else {
                            i.distanceName + " "
                        }
                    }

                    distancesTextView.text = selectedDistancesString.ifEmpty { "nothing" }
                    selectedDistancesString = ""
                }
                alertDialog.setCancelable(false)
                alertDialog.setPositiveButton(resources.getString(R.string.Ok)) { _, _ ->
                }
                alertDialog.show()

            }
            passengerNameInput.text?.clear()
        }
        deleteLastPassengerButton.setOnClickListener {

            if (listOfPassengers.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.nothing_to_delete),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                for (i in listOfPassengers.last().listOfPassengersSelectedDistances) {
                    i.passengersCount--
                }
                if (listOfPassengersSelectedChoice.isNotEmpty()) listOfPassengersSelectedChoice.removeLast()

                listOfPassengers.removeLast()

                if (listOfPassengersViews.isNotEmpty()) {
                    tableOfPassengers.removeView(listOfPassengersViews.last())
                    listOfPassengersViews.removeLast()
                    listOfPassengersIndex--
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.passenger_removed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        nextScreenButton.setOnClickListener {
            if (listOfPassengers.isEmpty() || listOfPassengersSelectedChoice.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.first_add_at_least_one_passenger),
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val fuelCostCalculator =
                    distancesList?.let { it1 -> FuelCostCalculator(it1, car) }

                if (fuelCostCalculator != null) {

                    if (car?.totalFuelCost == 0.0) {
                        car.totalFuelCost = fuelCostCalculator.calculateTotalCostOfFuel()
                    }
                    for (i in listOfPassengers) {
                        i.coveredDistance = fuelCostCalculator.calculatePassengerTotalDistance(i)
                        i.costOfFuel = fuelCostCalculator.calculatePassengerTotalCost(i)
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.error_when_calculating_fuel_cost),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                //move to activity where calculations are made
                val intent = Intent(this, Results::class.java)
                intent.putExtra("listOfPassengers", listOfPassengers)
                intent.putExtra("car", car)
                startActivity(intent)
            }
        }
        buttonPrevious.setOnClickListener {
            finish()
        }
    }
}


