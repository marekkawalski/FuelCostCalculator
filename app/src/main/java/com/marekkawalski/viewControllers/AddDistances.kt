package com.marekkawalski.viewControllers

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.marekkawalski.fuelcostcalculator.R
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
        title = "Fuel cost calculator"
        val addDistancesButton = findViewById<Button>(R.id.addDistancesButton)
        val deleteLastDistanceButton = findViewById<Button>(R.id.deleteLastButton)
        val nextScreenButton = findViewById<ImageButton>(R.id.buttonNextDistanceScreen)
        val tableOfDistances = findViewById<TableLayout>(R.id.tableLayout)
        val distanceNameInput = findViewById<TextInputEditText>(R.id.distanceNameInput)
        val distanceInput = findViewById<TextInputEditText>(R.id.distanceInput)
        var addingDistancesComplete = false

        addDistancesButton.setOnClickListener {
            addingDistancesComplete = false
            distanceName = distanceNameInput.text.toString()
            distance = distanceInput.text.toString().toDoubleOrNull()
            if (distanceName.isNullOrBlank()) {
                Toast.makeText(applicationContext, "Provide distance name!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (distance == null) {
                Toast.makeText(applicationContext, "Provide distance!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val distanceNameView = TextView(this)
            val distanceView = TextView(this)
            distanceNameView.text = distanceName
            distanceView.text = distance.toString()
            distanceNameView.gravity = Gravity.CENTER
            distanceView.gravity = Gravity.CENTER
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
                "Distance \"${distanceNameView.text}\" added!",
                Toast.LENGTH_SHORT
            ).show()
            distanceInput.text?.clear() //clear input field
            distanceNameInput.text?.clear() //clear input field
            addingDistancesComplete = true
        }
        deleteLastDistanceButton.setOnClickListener {
            if (listOfDistances.isNotEmpty()) listOfDistances.removeLast()
            else Toast.makeText(applicationContext, "Nothing to delete!", Toast.LENGTH_SHORT).show()

            if (listOfDistancesViews.isNotEmpty()) {
                tableOfDistances.removeView(listOfDistancesViews.last())
                listOfDistancesViews.removeLast()
                Toast.makeText(applicationContext, "Distance removed!", Toast.LENGTH_SHORT).show()
            }
        }
        nextScreenButton.setOnClickListener {
            if (!addingDistancesComplete) {
                Toast.makeText(
                    applicationContext,
                    "First add at least one distance!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //move to activity where calculations are made
                val intent = Intent(this, AddPassangers::class.java)
                intent.putExtra("listOfDistances", listOfDistances)
                startActivity(intent)
            }
        }
    }
}