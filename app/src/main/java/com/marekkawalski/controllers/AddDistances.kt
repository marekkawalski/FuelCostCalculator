package com.marekkawalski.controllers

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.marekkawalski.fuelcostcalculator.R
import model.Car
import model.Distance

/**
 * This class allows user to add new distances and select what cars were used on them.
 * @author Marek Kawalski
 * @version 1.6
 */
class AddDistances : AppCompatActivity() {
    /**
     *@property distanceName distance name
     */
    private var distanceName: String? = null

    /**
     *@property distance distance length in km, miles or any other unit
     */
    private var distance: Double? = null

    /**
     *@property listOfDistances list of distances that took place during an excursion
     */
    private var listOfDistances = ArrayList<Distance>()

    /**
     *@property listOfDistancesViews  list of table rows containing distances names, length and cars that were present on them
     */
    private var listOfDistancesViews = ArrayList<TableRow>()

    /**
     * @property choiceList list from which passenger can choose distances
     */
    private var choiceList = ArrayList<String>()

    /**
     * @property selectedChoiceList list connected with choiceList, it contains true if distance was chosen or false if not
     */
    private var selectedChoiceList = ArrayList<Boolean>()

    /**
     * @property listOfCarsSelectedChoice list of arrays containing information in which distances car took part
     */
    private var listOfCarsSelectedChoice = ArrayList<BooleanArray>()

    /**
     * @property distanceIndex index of distance
     */
    private var distanceIndex = -1

    /**
     * @property tableOfDistances table to which distances are added
     */
    private var tableOfDistances: TableLayout? = null

    /**
     * @property distanceNameInput input where user provides distance name, distance identifier or basically any string
     */
    private var distanceNameInput: TextInputEditText? = null

    /**
     * @property distanceInput input where user provides distance length. It can't be less equal to zero.
     */
    private var distanceInput: TextInputEditText? = null

    /**
     * @property carList list of all cars
     */
    private var carList: ArrayList<Car>? = null

    /**
     * @property selectedCarsString string containing all distances names (used for UI)
     */
    private var selectedCarsString = ""

    /**
     * @property choiceArray array from which passenger can choose distances
     */
    private var choiceArray: Array<String>? = null

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_distances)
        title = getString(R.string.app_full_name)

        val settings = SettingsController()
        settings.loadSettings(this)

        val addDistancesButton: Button? = findViewById(R.id.addPassengerButton)
        val deleteLastDistanceButton: Button? = findViewById(R.id.deleteLastButton)
        val nextScreenButton: ImageButton? = findViewById(R.id.buttonNextResults)
        val buttonPrevious: ImageButton? = findViewById(R.id.buttonPrevious)

        tableOfDistances = findViewById(R.id.tableOfCostsLayout)
        distanceNameInput = findViewById(R.id.passengerNameInput)
        distanceInput = findViewById(R.id.distanceInput)
        carList = intent.getParcelableArrayListExtra("listOfCars")

        val bottomNavigationView: BottomNavigationView? = findViewById(R.id.bottom_navigationView)
        bottomNavigationView?.selectedItemId = R.id.Car

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

        //by default all cars have all distances chosen
        carList?.forEach { i ->
            choiceList.add(i.carName)
            selectedChoiceList.add(true)
        }
        choiceArray = choiceList.toTypedArray()


        addDistancesButton?.setOnClickListener {

            handleAddingDistances()
        }

        deleteLastDistanceButton?.setOnClickListener {

            deleteLastDistanceHandler()
        }

        nextScreenButton?.setOnClickListener {

            moveToNextActivityHandler()
        }
        buttonPrevious?.setOnClickListener {
            finish()
        }
    }

    /**
     * Method handles adding new distances.
     *
     */
    private fun handleAddingDistances() {
        distanceName = distanceNameInput?.text.toString()
        distance = distanceInput?.text.toString().toDoubleOrNull()
        if (distanceName.isNullOrBlank()) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.provide_distance_name),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        if (distance == null) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.provide_distance),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (distance ?: 0.0 <= 0.0) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.numberMustBePositive),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        //create new distance
        val distanceInstance = Distance(distanceName as String, distance ?: 0.0, ++distanceIndex)
        //add this distance to list of all distances
        listOfDistances.add(distanceInstance)

        //add elements to UI
        val distanceNameView = TextView(this)
        val distanceView = TextView(this)
        val carListView = Button(this)
        val tableRow = TableRow(this)

        distanceNameView.text = distanceName
        distanceNameView.gravity = Gravity.CENTER
        distanceNameView.width = ViewGroup.LayoutParams.WRAP_CONTENT

        distanceView.text = distance.toString()
        distanceView.gravity = Gravity.CENTER
        distanceView.width = ViewGroup.LayoutParams.WRAP_CONTENT

        carListView.text = distance.toString()
        carListView.gravity = Gravity.CENTER
        carListView.width = ViewGroup.LayoutParams.WRAP_CONTENT

        listOfDistancesViews.add(tableRow)

        val selectedChoiceArray = selectedChoiceList.toBooleanArray()
        listOfCarsSelectedChoice.add(selectedChoiceArray)


        //by default add all cars to particular distance
        carList?.forEach { i ->
            distanceInstance.listOfCars.add(i)
        }

        whatCarsWereUsedOnADistance(carListView, distanceInstance)

        handleAlertDialogs(carListView, distanceInstance)

        tableRow.addView(distanceNameView)
        tableRow.addView(distanceView)
        tableRow.addView(carListView)
        tableOfDistances?.addView(tableRow)

        carListView.setOnClickListener {

            handleAlertDialogs(carListView, distanceInstance)
        }

        distanceInput?.text?.clear() //clear input field
        distanceNameInput?.text?.clear() //clear input field
    }

    /**
     * Method handles deleting last distance.
     *
     */
    private fun deleteLastDistanceHandler() {
        if (listOfDistances.isNotEmpty()) {
            distanceIndex--
            listOfDistances.removeLast()

        } else Toast.makeText(
            applicationContext,
            resources.getString(R.string.nothing_to_delete),
            Toast.LENGTH_SHORT
        )
            .show()

        if (listOfDistancesViews.isNotEmpty()) {
            tableOfDistances?.removeView(listOfDistancesViews.last())
            listOfDistancesViews.removeLast()
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.distance_removed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Method is responsible for displaying alert dialogs on screen. In these alert dialogs, user can add what cars took part in
     * various distances.
     *
     * @param carListView button on which cars that took part in certain distance are shown
     * @param distanceInstance particular distance
     */
    private fun handleAlertDialogs(carListView: Button, distanceInstance: Distance?) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(
            resources.getString(R.string.chooseCarsFor) + " " + listOfDistances[distanceInstance?.id
                ?: return].distanceName
        )
        alertDialog.setMultiChoiceItems(
            choiceArray,
            listOfCarsSelectedChoice[distanceInstance.id]
        ) { _: DialogInterface, position: Int, check: Boolean ->

            listOfCarsSelectedChoice[distanceInstance.id][position] = check

            if (check) {
                listOfDistances[distanceInstance.id].listOfCars.add(
                    carList?.get(
                        position
                    ) ?: return@setMultiChoiceItems

                )
            } else {
                listOfDistances[distanceInstance.id].listOfCars.remove(
                    carList?.get(
                        position
                    ) ?: return@setMultiChoiceItems
                )
            }
            whatCarsWereUsedOnADistance(carListView, distanceInstance)
        }
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(resources.getString(R.string.Ok)) { _, _ ->
        }
        alertDialog.show()
    }

    /**
     * Save what cars were used on particular distances.
     *
     * @param carListView button on which cars that took part in certain distance are shown
     * @param distanceInstance current distance
     */
    private fun whatCarsWereUsedOnADistance(carListView: Button, distanceInstance: Distance?) {
        for (i in distanceInstance?.listOfCars ?: return) {
            selectedCarsString += if (i != distanceInstance.listOfCars.last()) {
                i?.carName + ", "
            } else {
                i?.carName + " "
            }
        }
        carListView.text = selectedCarsString.ifEmpty { resources.getString(R.string.nothing) }

        selectedCarsString = ""
    }

    /**
     * Method to handle moving to next activity.
     *
     */
    private fun moveToNextActivityHandler() {
        if (listOfDistancesViews.isEmpty() || listOfDistances.isEmpty()) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.first_add_at_least_one_distance),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            intent.getBooleanExtra("dontKnowCost", false)
            //move to activity where calculations are made
            val intent = Intent(this, AddPassengers::class.java)
            intent.putExtra("listOfDistances", listOfDistances)
            intent.putExtra("listOfCars", carList)
            startActivity(intent)
        }
    }
}