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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_distances)
        title = getString(R.string.app_full_name)

        val settings = SettingsController()
        settings.loadSettings(this, resources)

        val addDistancesButton = findViewById<Button>(R.id.addPassengerButton)
        val deleteLastDistanceButton = findViewById<Button>(R.id.deleteLastButton)
        val nextScreenButton = findViewById<ImageButton>(R.id.buttonNextResults)
        val tableOfDistances = findViewById<TableLayout>(R.id.tableOfCostsLayout)
        val distanceNameInput = findViewById<TextInputEditText>(R.id.passengerNameInput)
        val distanceInput = findViewById<TextInputEditText>(R.id.distanceInput)
        val buttonPrevious = findViewById<ImageButton>(R.id.buttonPrevious)
        val carList = intent.getParcelableArrayListExtra<Car>("listOfCars")
        var selectedCarsString = ""

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigationView)
        bottomNavigationView.selectedItemId = R.id.Car


        bottomNavigationView.setOnItemSelectedListener { item ->
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

        if (carList != null) {
            for (i in carList) {
                choiceList.add(i.carName)
                selectedChoiceList.add(true)
            }
        }
        val choiceArray = choiceList.toTypedArray()

        addDistancesButton.setOnClickListener {
            distanceName = distanceNameInput.text.toString()
            distance = distanceInput.text.toString().toDoubleOrNull()
            if (distanceName.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.provide_distance_name),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            if (distance == null) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.provide_distance),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
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
            listOfDistances.add(distanceInstance ?: return@setOnClickListener)

            //by default add all cars to particular distance
            if (carList != null) {
                for (i in carList) {
                    distanceInstance?.listOfCars?.add(i)
                }
            }
            for (i in distanceInstance?.listOfCars ?: return@setOnClickListener) {
                selectedCarsString += if (i != distanceInstance?.listOfCars?.last()) {
                    i?.carName + ", "
                } else {
                    i?.carName + " "
                }
            }
            carListView.text = selectedCarsString.ifEmpty { "nothing" }

            selectedCarsString = ""

            var alertDialog = AlertDialog.Builder(this)
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
                for (i in distanceInstance?.listOfCars ?: return@setMultiChoiceItems) {
                    selectedCarsString += if (i != distanceInstance?.listOfCars?.last()) {
                        i?.carName + ", "
                    } else {
                        i?.carName + " "
                    }
                }
                carListView.text = selectedCarsString.ifEmpty { "nothing" }
                selectedCarsString = ""
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

            //create new table row and add three columns: distance name, distance(km, miles), list of cars
            tableRow = TableRow(this)
            tableRow?.addView(distanceNameView)
            tableRow?.addView(distanceView)
            tableRow?.addView(carListView)
            tableOfDistances.addView(tableRow)
            listOfDistancesViews.add(tableRow ?: return@setOnClickListener)

            distanceInput.text?.clear() //clear input field
            distanceNameInput.text?.clear() //clear input field

            carListView.setOnClickListener myOnclickListener@{
                alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle(
                    resources.getString(R.string.chooseCarsFor) + listOfDistances[distanceInstance?.id
                        ?: return@myOnclickListener].distanceName
                )
                alertDialog.setMultiChoiceItems(
                    choiceArray,
                    listOfCarsSelectedChoice[distanceInstance?.id ?: return@myOnclickListener]
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
                    for (i in distanceInstance?.listOfCars ?: return@setMultiChoiceItems) {
                        selectedCarsString += if (i != distanceInstance?.listOfCars?.last()) {
                            i?.carName + ", "
                        } else {
                            i?.carName + " "
                        }
                    }
                    carListView.text = selectedCarsString.ifEmpty { "nothing" }

                    selectedCarsString = ""
                }
                alertDialog.setCancelable(false)
                alertDialog.setPositiveButton(resources.getString(R.string.Ok)) { _, _ ->
                }
                alertDialog.show()
            }
        }

        deleteLastDistanceButton.setOnClickListener {
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
                tableOfDistances.removeView(listOfDistancesViews.last())
                listOfDistancesViews.removeLast()
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.distance_removed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        nextScreenButton.setOnClickListener {
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
        buttonPrevious.setOnClickListener {
            finish()
        }
    }
}