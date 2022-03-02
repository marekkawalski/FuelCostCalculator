package com.marekkawalski.viewControllers

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.marekkawalski.fuelcostcalculator.R
import model.Car
import model.Distance

class AddDistances : AppCompatActivity() {
    private var distanceName: String? = null
    private var distance: Double? = null
    private var tableRow: TableRow? = null
    private var listOfDistances = ArrayList<Distance>()
    private var listOfDistancesViews = ArrayList<TableRow>()
    private var distanceInstance: Distance? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_distances)
        title = getString(R.string.app_full_name)
        val addDistancesButton = findViewById<Button>(R.id.addPassengerButton)
        val deleteLastDistanceButton = findViewById<Button>(R.id.deleteLastButton)
        val nextScreenButton = findViewById<ImageButton>(R.id.buttonNextResults)
        val tableOfDistances = findViewById<TableLayout>(R.id.tableOfCostsLayout)
        val distanceNameInput = findViewById<TextInputEditText>(R.id.passengerNameInput)
        val distanceInput = findViewById<TextInputEditText>(R.id.distanceInput)
        var addingDistancesComplete = false

        addDistancesButton.setOnClickListener {
            addingDistancesComplete = false
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

            distanceNameView.text = distanceName
            distanceNameView.gravity = Gravity.CENTER
            distanceNameView.width = ViewGroup.LayoutParams.WRAP_CONTENT

            distanceView.text = distance.toString()
            distanceView.gravity = Gravity.CENTER
            distanceView.width = ViewGroup.LayoutParams.WRAP_CONTENT

            tableRow = TableRow(this)
            tableRow?.addView(distanceNameView)
            tableRow?.addView(distanceView)
            tableOfDistances.addView(tableRow)
            listOfDistancesViews.add(tableRow ?: return@setOnClickListener)
            distanceInstance = Distance(distanceName as String, distance ?: 0.0)
            listOfDistances.add(distanceInstance ?: return@setOnClickListener)

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
            distanceInput.text?.clear() //clear input field
            distanceNameInput.text?.clear() //clear input field
            addingDistancesComplete = true
        }
        deleteLastDistanceButton.setOnClickListener {
            if (listOfDistances.isNotEmpty()) {
                listOfDistances.removeLast()
                //counter--
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
            if (!addingDistancesComplete || listOfDistancesViews.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.first_add_at_least_one_distance),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val car = intent.getParcelableExtra<Car>("car")
                intent.getBooleanExtra("dontKnowCost", false)
                //move to activity where calculations are made
                val intent = Intent(this, AddPassengers::class.java)
                intent.putExtra("listOfDistances", listOfDistances)
                intent.putExtra("car", car)
                startActivity(intent)
            }
        }
    }
}