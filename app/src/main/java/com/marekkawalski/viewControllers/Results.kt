package com.marekkawalski.viewControllers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.marekkawalski.fuelcostcalculator.R
import model.FuelCostCalculator
import model.Person
import otherControllers.SettingsController
import kotlin.math.roundToInt

class Results : AppCompatActivity() {

    private var listOfPayers = ArrayList<Person>()
    private var listOfPeopleToBePaid = ArrayList<Person>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_results)
        title = getString(R.string.app_full_name)

        val settings = SettingsController()
        settings.loadSettings(this, resources)

        val listOfPassengers = intent.getParcelableArrayListExtra<Person>("listOfPassengers")
        val tableOfPassengers = findViewById<TableLayout>(R.id.tableOfCostsLayout)
        val buttonPrevious = findViewById<ImageButton>(R.id.buttonPrevious)


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

        if (listOfPassengers != null) {
            for (person in listOfPassengers) {
                person.howMuchPaid -= person.costOfFuel
                when {
                    person.howMuchPaid > 0.0 -> {
                        listOfPeopleToBePaid.add(person)
                    }
                    person.howMuchPaid < 0.0 -> {
                        listOfPayers.add(person)
                    }
                }
            }

            listOfPayers.sortBy { passenger -> passenger.howMuchPaid }
            listOfPeopleToBePaid.sortBy { passenger -> passenger.howMuchPaid }
            listOfPeopleToBePaid.reverse()

            Log.i("sorted", "------------listOfPayers------------\n")
            for (person in listOfPayers) {
                Log.i("sorted", person.name + " " + person.howMuchPaid.toString() + "\n")
            }
            Log.i("sorted", "------------listOfPeopleToBePaid------------\n")
            for (person in listOfPeopleToBePaid) {
                Log.i("sorted", person.name + " " + person.howMuchPaid.toString() + "\n")
            }

            Log.i("sorted", "------------payers------------\n")

            val fuelCostCalculator = FuelCostCalculator()
            fuelCostCalculator.whoPassengerShouldPay(listOfPayers, listOfPeopleToBePaid)

            for (person in listOfPassengers) {
                if (person.mapOfPayments.isEmpty()) {
                    val passengerNameView = TextView(this)
                    val howMuchTextView = TextView(this)
                    val wireToTextView = TextView(this)
                    val tableRow = TableRow(this)

                    passengerNameView.gravity = Gravity.CENTER
                    passengerNameView.width = WRAP_CONTENT
                    passengerNameView.textSize = 16F

                    howMuchTextView.gravity = Gravity.CENTER
                    howMuchTextView.width = WRAP_CONTENT
                    howMuchTextView.textSize = 16F

                    wireToTextView.gravity = Gravity.CENTER
                    wireToTextView.width = WRAP_CONTENT
                    wireToTextView.textSize = 16F

                    passengerNameView.text = person.name
                    wireToTextView.text = "-"
                    howMuchTextView.text =
                        ((person.howMuchPaid * 100.0).roundToInt() / 100.0).toString()

                    tableRow.addView(passengerNameView)
                    tableRow.addView(wireToTextView)
                    tableRow.addView(howMuchTextView)
                    tableOfPassengers.addView(tableRow)


                }
                for ((key, value) in person.mapOfPayments) {
                    val passengerNameView = TextView(this)
                    val howMuchTextView = TextView(this)
                    val wireToTextView = TextView(this)
                    val tableRow = TableRow(this)

                    passengerNameView.gravity = Gravity.CENTER
                    passengerNameView.width = WRAP_CONTENT
                    passengerNameView.textSize = 16F

                    howMuchTextView.gravity = Gravity.CENTER
                    howMuchTextView.width = WRAP_CONTENT
                    howMuchTextView.textSize = 16F

                    wireToTextView.gravity = Gravity.CENTER
                    wireToTextView.width = WRAP_CONTENT
                    wireToTextView.textSize = 16F

                    passengerNameView.text = person.name
                    wireToTextView.text = key.name
                    howMuchTextView.text = ((value * 100.0).roundToInt() / 100.0).toString()

                    tableRow.addView(passengerNameView)
                    tableRow.addView(wireToTextView)
                    tableRow.addView(howMuchTextView)
                    tableOfPassengers.addView(tableRow)
                }
            }
        }
        buttonPrevious.setOnClickListener {
            finish()
        }

    }

}