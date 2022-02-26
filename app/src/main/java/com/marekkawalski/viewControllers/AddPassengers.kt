package com.marekkawalski.viewControllers

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.marekkawalski.fuelcostcalculator.R
import model.Car
import model.Distance
import model.Person


class AddPassengers : AppCompatActivity() {

    private var passengerName: String? = null
    private var listOfPassengers = ArrayList<Person?>()
    private var choiceList = ArrayList<String>()
    private var selectedChoiceList = ArrayList<Boolean>()
    private var listOfPassengersSelectedChoice = ArrayList<BooleanArray>()
    private var listOfPassengersViews = ArrayList<TableRow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_passangers)
        title = "Fuel cost calculator"
        var listOfPassengersIndex = -1

        val car = intent.getParcelableExtra<Car>("car")
        val distancesList = intent.getParcelableArrayListExtra<Distance>("listOfDistances")
        val addPassengerButton = findViewById<Button>(R.id.addPassengerButton)
        val deleteLastPassengerButton = findViewById<Button>(R.id.deleteLastButton)
        val nextScreenButton = findViewById<ImageButton>(R.id.buttonNextResults)
        val tableOfPassengers = findViewById<TableLayout>(R.id.tableLayout)
        val passengerNameInput = findViewById<TextInputEditText>(R.id.passengerNameInput)
        val passengersTextView = findViewById<TextView>(R.id.passengersTextView)

        passengersTextView.text = "Passengers \n" + car?.carName
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
                Toast.makeText(applicationContext, "Provide passenger name!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val passengerNameView = TextView(this)
            val distancesTextView = Button(this)
            val tableRow = TableRow(this)
            passengerNameView.text = passengerName
            passengerNameView.gravity = Gravity.CENTER
            passengerNameView.width = WRAP_CONTENT

            distancesTextView.gravity = Gravity.CENTER
            distancesTextView.width = WRAP_CONTENT
            distancesTextView.text = "Tap to change"

            listOfPassengersViews.add(tableRow)

            val selectedChoiceArray = selectedChoiceList.toBooleanArray()
            listOfPassengersSelectedChoice.add(selectedChoiceArray)

            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Choose distances for $passengerName")
            alertDialog.setMultiChoiceItems(
                choiceArray,
                selectedChoiceArray
            ) { _: DialogInterface, position: Int, check: Boolean ->
                listOfPassengersSelectedChoice.last()[position] = check
            }
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Ok") { _, _ ->
            }
            alertDialog.show()

            val person = Person(passengerName as String, ++listOfPassengersIndex)
            listOfPassengers.add(person)

            tableRow.addView(passengerNameView)
            tableRow.addView(distancesTextView)
            tableOfPassengers.addView(tableRow)

            distancesTextView.setOnClickListener {
                alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle(
                    "Choose distances for " + (listOfPassengers[person.id]?.name
                        ?: "")
                )
                alertDialog.setMultiChoiceItems(
                    choiceArray,
                    listOfPassengersSelectedChoice[person.id]
                ) { _: DialogInterface, position: Int, check: Boolean ->
                    listOfPassengersSelectedChoice[person.id][position] = check
                }
                alertDialog.setCancelable(false)
                alertDialog.setPositiveButton("Ok") { _, _ ->
                }
                alertDialog.show()

            }
            passengerNameInput.text?.clear()
        }
        deleteLastPassengerButton.setOnClickListener {

            if (listOfPassengers.isNotEmpty()) listOfPassengers.removeLast()
            else Toast.makeText(applicationContext, "Nothing to delete!", Toast.LENGTH_SHORT).show()

            if (listOfPassengersSelectedChoice.isNotEmpty()) listOfPassengersSelectedChoice.removeLast()

            if (listOfPassengersViews.isNotEmpty()) {
                tableOfPassengers.removeView(listOfPassengersViews.last())
                listOfPassengersViews.removeLast()
                listOfPassengersIndex--
                Toast.makeText(applicationContext, "Passenger removed!", Toast.LENGTH_SHORT).show()
            }
        }
        nextScreenButton.setOnClickListener {
            // @toDo
        }
    }
}


