package com.marekkawalski.viewControllers

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
import otherControllers.SettingsController

/**
 * Add distances
 * This class allows user to add new distances and select what cars were used on them.
 * @author Marek Kawalski
 */
class AddDistances : AppCompatActivity() {
    private var distanceName: String? = null
    private var distance: Double? = null
    private var listOfDistances = ArrayList<Distance>()
    private var listOfDistancesViews = ArrayList<TableRow>()
    private var choiceList = ArrayList<String>()
    private var selectedChoiceList = ArrayList<Boolean>()
    private var listOfCarsSelectedChoice = ArrayList<BooleanArray>()
    private var distanceIndex = -1
    private var tableOfDistances: TableLayout? = null
    private var distanceNameInput: TextInputEditText? = null
    private var distanceInput: TextInputEditText? = null
    private var carList: ArrayList<Car>? = null
    private var selectedCarsString = ""
    private var choiceArray: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_distances)
        title = getString(R.string.app_full_name)

        val settings = SettingsController()
        settings.loadSettings(this, resources)

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
     * Method handles adding new distances
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
     * Delete last distance handler
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
     * @param carListView
     */
    private fun whatCarsWereUsedOnADistance(carListView: Button, distanceInstance: Distance?) {
        for (i in distanceInstance?.listOfCars ?: return) {
            selectedCarsString += if (i != distanceInstance.listOfCars.last()) {
                i?.carName + ", "
            } else {
                i?.carName + " "
            }
        }
        carListView.text = selectedCarsString.ifEmpty { "nothing" }

        selectedCarsString = ""
    }

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