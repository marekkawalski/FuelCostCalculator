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
 * @version 1.7
 */
class AddPassengers : AppCompatActivity() {
    /**
     * @property passengerName name of passenger
     */
    private var passengerName: String? = null

    /**
     * @property listOfPassengers list of passengers
     */
    private var listOfPassengers = ArrayList<Person>()

    /**
     * @property choiceList list of distances to choose from for each passenger
     */
    private var choiceList = ArrayList<String>()

    /**
     * @property selectedChoiceList list of arrays which indicate who was present on what distances
     */
    private var selectedChoiceList = ArrayList<Boolean>()

    /**
     * @property listOfPassengersSelectedChoice list of arrays which indicates at what distances passengers were present
     */
    private var listOfPassengersSelectedChoice = ArrayList<BooleanArray>()

    /**
     * @property listOfPassengersViews list of table rows
     */
    private var listOfPassengersViews = ArrayList<TableRow>()

    /**
     * @property listOfPassengersIndex passenger id
     */
    private var listOfPassengersIndex = -1

    /**
     * @property tableOfPassengers table which is used to display passengers and their selected choices
     */
    private var tableOfPassengers: TableLayout? = null

    /**
     *@property passengerNameInput input where passenger provides their name
     */
    private var passengerNameInput: TextInputEditText? = null

    /**
     * @property selectedDistancesString string containing all distances selected by a passenger
     */
    private var selectedDistancesString = ""

    /**
     * @property distancesList list of all distances
     */
    private var distancesList: ArrayList<Distance?>? = null

    /**
     * @property listOfCars list of all cars
     */
    private var listOfCars: ArrayList<Car?>? = null

    /**
     * @property choiceArray array of choices
     */
    private var choiceArray: Array<String>? = null

    /**
     * On create
     *
     * @param savedInstanceState
     */
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
     * Method handles adding passengers.
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
     * Method stores distances names.
     *
     * @param person person whose distances is to be stored
     * @param distancesTextView where to display the distances
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
     * Method displays alert dialogs in which user can select distances covered by each passenger.
     *
     * @param person person to select distances for
     * @param distancesTextView where to display the distances
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
     * Method handles deleting last passenger.
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
     * Method handles moving to next activity.
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


