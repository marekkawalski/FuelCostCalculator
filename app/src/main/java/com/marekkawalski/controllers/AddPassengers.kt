package com.marekkawalski.controllers

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
import model.*

/**
 * Add passengers
 * @author Marek Kawalski
 * @version 1.6
 */
class AddPassengers : AppCompatActivity() {

    private var passengerName: String? = null
    private var listOfPassengers = ArrayList<Person>()
    private var choiceList = ArrayList<String>()
    private var selectedChoiceList = ArrayList<Boolean>()
    private var listOfPassengersSelectedChoice = ArrayList<BooleanArray>()
    private var listOfPassengersViews = ArrayList<TableRow>()
    private var listOfPassengersIndex = -1
    private var tableOfPassengers: TableLayout? = null
    private var passengerNameInput: TextInputEditText? = null
    private var selectedDistancesString = ""
    private var distancesList: ArrayList<Distance?>? = null
    private var listOfCars: ArrayList<Car?>? = null
    private var choiceArray: Array<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = SettingsController()
        settings.loadSettings(this)

        setContentView(R.layout.activity_add_passangers)
        title = getString(R.string.app_full_name)

        val bottomNavigationView: BottomNavigationView? = findViewById(R.id.bottom_navigationView)
        bottomNavigationView?.selectedItemId = R.id.Car

        //bottom navigation
        bottomNavigationView?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Car -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        distancesList = intent.getParcelableArrayListExtra("listOfDistances")
        listOfCars = intent.getParcelableArrayListExtra("listOfCars")

        val addPassengerButton: Button? = findViewById(R.id.addPassengerButton)
        val deleteLastPassengerButton: Button? = findViewById(R.id.deleteLastButton)
        val nextScreenButton: ImageButton? = findViewById(R.id.buttonNextResults)
        val buttonPrevious: ImageButton? = findViewById(R.id.buttonPrevious)
        selectedDistancesString = ""

        tableOfPassengers = findViewById(R.id.tableOfCostsLayout)
        passengerNameInput = findViewById(R.id.passengerNameInput)

        //Add all distances to list that stores their names
        //by default, each passenger takes part in all distances
        distancesList?.forEach { i ->
            i?.distanceName?.let { choiceList.add(it) }
            selectedChoiceList.add(true)
        }
        choiceArray = choiceList.toTypedArray()

        //listen to addPassengerButton click
        addPassengerButton?.setOnClickListener {

            addPassengersHandler()
        }
        //listen to deleteLastPassengerButton click
        deleteLastPassengerButton?.setOnClickListener {

            deleteLastPassengerHandler()
        }
        //wait for nextScreenButton click
        nextScreenButton?.setOnClickListener {

            movingToNextScreenHandler()
        }
        //come back to previous activity
        buttonPrevious?.setOnClickListener {

            finish()
        }
    }

    /**
     * Add passengers handler
     *
     */
    private fun addPassengersHandler() {
        passengerName = passengerNameInput?.text.toString()
        //check is name was provided
        if (passengerName.isNullOrBlank()) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.provide_passenger_name),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        //create new person
        val person = Person(passengerName as String, ++listOfPassengersIndex)
        //add that person to list of passengers
        listOfPassengers.add(person)

        //add elements to UI
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

        //add all distances to passenger selected distances list
        distancesList?.forEach { i ->
            i?.let { person.listOfPassengersSelectedDistances.add(it) }
            //increment passengers count on added distance
            i?.let { it -> it.passengersCount++ }
        }
        //store distances names so as to display them later on screen
        storeDistancesNames(person, distancesTextView)

        //create popup alert dialog where passenger can choose distances in which they took part
        handleDistancesAlertDialogs(
            person,
            distancesTextView
        )

        tableRow.addView(passengerNameView)
        tableRow.addView(distancesTextView)
        tableOfPassengers?.addView(tableRow)

        //set on click listener for newly added button, so as to give
        //the user a chance to change distances at any time
        distancesTextView.setOnClickListener {
            handleDistancesAlertDialogs(
                person,
                distancesTextView
            )
        }
        passengerNameInput?.text?.clear()
    }

    /**
     * Store distances names
     *
     * @param person
     * @param distancesTextView
     */
    private fun storeDistancesNames(person: Person, distancesTextView: TextView) {
        for (i in person.listOfPassengersSelectedDistances) {
            selectedDistancesString += if (i != person.listOfPassengersSelectedDistances.last()) {
                i.distanceName + ", "
            } else {
                i.distanceName + " "
            }
        }
        distancesTextView.text =
            selectedDistancesString.ifEmpty { resources.getString(R.string.nothing) }
        selectedDistancesString = ""
    }

    /**
     * Handle distances alert dialogs
     *
     * @param person
     * @param distancesTextView
     */
    private fun handleDistancesAlertDialogs(
        person: Person,
        distancesTextView: TextView
    ) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(resources.getString(R.string.choose_distances_for) + " " + listOfPassengers[person.id].name)
        alertDialog.setMultiChoiceItems(
            choiceArray,
            listOfPassengersSelectedChoice[person.id]
        ) { _: DialogInterface, position: Int, check: Boolean ->
            listOfPassengersSelectedChoice[person.id][position] = check

            //if checkbox is checked
            if (check) {
                //add current distance to passenger selected distances list
                listOfPassengers[person.id].listOfPassengersSelectedDistances.add(
                    distancesList?.get(
                        position
                    ) ?: return@setMultiChoiceItems
                )
                //increment passenger count

                distancesList?.let { it1 -> it1[position]?.let { it2 -> it2.passengersCount++ } }
            }
            //if checkbox is unchecked
            else {
                //remove current distance to passenger selected distances list
                listOfPassengers[person.id].listOfPassengersSelectedDistances.remove(
                    distancesList?.get(
                        position
                    ) ?: return@setMultiChoiceItems
                )
                //decrement passenger count
                distancesList?.let { it1 -> it1[position]?.let { it2 -> it2.passengersCount-- } }
            }
            //change distances which will appear in UI
            storeDistancesNames(person, distancesTextView)

        }
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Ok") { _, _ ->
        }
        alertDialog.show()
    }

    /**
     * Delete last passenger handler
     *
     */
    private fun deleteLastPassengerHandler() {
        //make sure if there is anything to delete
        if (listOfPassengers.isEmpty()) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.nothing_to_delete),
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            for (distance in listOfPassengers.last().listOfPassengersSelectedDistances) {
                //decrement passenger count on the distance where passenger was present
                distance.passengersCount--
            }
            //remove last list from list of passenger selectedChoice list
            if (listOfPassengersSelectedChoice.isNotEmpty()) listOfPassengersSelectedChoice.removeLast()

            //delete last passenger from passenger list
            listOfPassengers.removeLast()

            //remove passenger from UI
            if (listOfPassengersViews.isNotEmpty()) {
                tableOfPassengers?.removeView(listOfPassengersViews.last())
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

    /**
     * Moving to next screen handler
     *
     */
    private fun movingToNextScreenHandler() {
        if (listOfPassengers.isEmpty() || listOfPassengersSelectedChoice.isEmpty()) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.first_add_at_least_one_passenger),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            //create model instance
            val fuelCostCalculator =
                distancesList?.let { it1 ->
                    listOfCars?.let { it2 ->
                        FuelCostCalculator(
                            it1,
                            it2
                        )
                    }
                }
            //check if model class is not null
            //model class can be null if list of distances of list of cars is null
            if (fuelCostCalculator != null) {

                //if cost of fuel haven't been then calculate it
                fuelCostCalculator.calculateTotalCostOfFuel()
                //calculate total distance covered by each passenger
                //with covered distance calculated calculate total cost of fuel
                for (person in listOfPassengers) {
                    person.coveredDistance =
                        fuelCostCalculator.calculatePassengerTotalDistance(person)
                    try {
                        person.costOfFuel = fuelCostCalculator.calculatePassengerTotalCost(person)
                    } catch (ex: FuelCostCalculatorException) {
                        Toast.makeText(
                            applicationContext,
                            ex.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.error_when_calculating_fuel_cost),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            //move to next activity
            val intent = Intent(this, AddPayers::class.java)
            intent.putExtra("listOfPassengers", listOfPassengers)
            intent.putExtra("listOfCars", listOfCars)
            startActivity(intent)
        }
    }
}


