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
    private var tableRow: TableRow? = null
    private var listOfDistances = ArrayList<Distance>()
    private var listOfDistancesViews = ArrayList<TableRow>()
    private var distanceInstance: Distance? = null
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
        if (carList != null) {
            carList?.forEach { i ->
                choiceList.add(i.carName)
                selectedChoiceList.add(true)
            }
        }
        choiceArray = choiceList.toTypedArray()

        addDistancesButton?.setOnClickListener {
            handleAddingDistances()
        }

        deleteLastDistanceButton?.setOnClickListener {
            deleteLastDistanceHandler()
        }

        nextScreenButton?.setOnClickListener {
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

        val distanceNameView = TextView(this)
        val distanceView = TextView(this)
        val carListView = Button(this)

        distanceNameView.text = distanceName
        distanceNameView.gravity = Gravity.CENTER
        distanceNameView.width = ViewGroup.LayoutParams.WRAP_CONTENT

        distanceView.text = distance.toString()
        distanceView.gravity = Gravity.CENTER
        distanceView.width = ViewGroup.LayoutParams.WRAP_CONTENT

        carListView.text = distance.toString()
        carListView.gravity = Gravity.CENTER
        carListView.width = ViewGroup.LayoutParams.WRAP_CONTENT

        val selectedChoiceArray = selectedChoiceList.toBooleanArray()
        listOfCarsSelectedChoice.add(selectedChoiceArray)

        //create new distance
        distanceInstance = Distance(distanceName as String, distance ?: 0.0, ++distanceIndex)
        //add this distance to list of all distances
        listOfDistances.add(distanceInstance ?: return)

        //by default add all cars to particular distance
        if (carList != null) {
            carList?.forEach { i ->
                distanceInstance?.listOfCars?.add(i)
            }
        }

        whatCarsWereUsedOnADistance(carListView)

        showDefaultAlertDialog(carListView, distanceNameView, selectedChoiceArray)

        //create new table row and add three columns: distance name, distance(km, miles), list of cars
        tableRow = TableRow(this)
        tableRow?.addView(distanceNameView)
        tableRow?.addView(distanceView)
        tableRow?.addView(carListView)
        tableOfDistances?.addView(tableRow)
        listOfDistancesViews.add(tableRow ?: return)

        distanceInput?.text?.clear() //clear input field
        distanceNameInput?.text?.clear() //clear input field

        carListView.setOnClickListener {
            handleChangeDistancesAlertDialog(carListView)
        }
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

    /**
     * Show default alert dialog
     * Method shows popup in which user chooses which distances a car covered
     *
     * @param carListView
     * @param distanceNameView
     * @param selectedChoiceArray
     */
    private fun showDefaultAlertDialog(
        carListView: Button,
        distanceNameView: TextView,
        selectedChoiceArray: BooleanArray
    ) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("${resources.getString(R.string.chooseCarsFor)} ${distanceName.toString()}")
        alertDialog.setMultiChoiceItems(
            choiceArray,
            selectedChoiceArray
        ) { _: DialogInterface, position: Int, check: Boolean ->
            listOfCarsSelectedChoice.last()[position] = check

            if (check) {
                distanceInstance?.listOfCars?.add(
                    carList?.get(
                        position
                    ) ?: return@setMultiChoiceItems
                )

            } else {
                distanceInstance?.listOfCars?.remove(
                    carList?.get(
                        position
                    ) ?: return@setMultiChoiceItems
                )
            }
            whatCarsWereUsedOnADistance(carListView)
        }
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Ok") { _, _ ->
            //if everything goes smoothly
            Toast.makeText(
                applicationContext,
                "${resources.getString(R.string.distance)} \"${distanceNameView.text}\" ${
                    resources.getString(
                        R.string.added
                    )
                }",
                Toast.LENGTH_SHORT
            ).show()
        }
        alertDialog.show()
    }

    /**
     * Handle change distances alert dialog
     * Method handles changing what cars took part in particular distances. It displays a popup in which user can change
     * values chosen right after distance had been added/
     * @param carListView
     */
    private fun handleChangeDistancesAlertDialog(carListView: Button) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(
            resources.getString(R.string.chooseCarsFor) + listOfDistances[distanceInstance?.id
                ?: return].distanceName
        )
        alertDialog.setMultiChoiceItems(
            choiceArray,
            listOfCarsSelectedChoice[distanceInstance?.id ?: return]
        ) { _: DialogInterface, position: Int, check: Boolean ->

            if (check) {
                listOfCarsSelectedChoice[distanceInstance?.id!!][position] = true
                listOfDistances[distanceInstance?.id!!].listOfCars.add(
                    carList?.get(
                        position
                    ) ?: return@setMultiChoiceItems

                )
            } else {
                listOfCarsSelectedChoice[distanceInstance?.id!!][position] = false
                listOfDistances[distanceInstance?.id!!].listOfCars.remove(
                    carList?.get(
                        position
                    ) ?: return@setMultiChoiceItems
                )
            }
            whatCarsWereUsedOnADistance(carListView)
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
    private fun whatCarsWereUsedOnADistance(carListView: Button) {
        for (i in distanceInstance?.listOfCars ?: return) {
            selectedCarsString += if (i != distanceInstance?.listOfCars?.last()) {
                i?.carName + ", "
            } else {
                i?.carName + " "
            }
        }
        carListView.text = selectedCarsString.ifEmpty { "nothing" }

        selectedCarsString = ""
    }
}