package com.marekkawalski.viewControllers

import android.content.Intent
import android.os.Bundle
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
import java.util.*
import kotlin.math.roundToInt

/**
 * Results
 * Class is responsible for displaying who passengers should pay and how much
 * @author Marek Kawalski
 */
class Results : AppCompatActivity() {

    private var listOfPayers = ArrayList<Person>()
    private var listOfPeopleToBePaid = ArrayList<Person>()
    private var tableOfPassengers: TableLayout? = null
    private var listOfPassengers: ArrayList<Person>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_results)
        title = getString(R.string.app_full_name)

        val settings = SettingsController()
        settings.loadSettings(this, resources)

        listOfPassengers = intent.getParcelableArrayListExtra("listOfPassengers")
        tableOfPassengers = findViewById(R.id.tableOfCostsLayout)
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

        addPassengersToListOfPayersOrToBePaid()

        val fuelCostCalculator = FuelCostCalculator()
        fuelCostCalculator.whoPassengerShouldPay(listOfPayers, listOfPeopleToBePaid)


        listOfPassengers?.sortBy { person -> person.name.uppercase(Locale.getDefault()) }

        listOfPassengers?.forEach { person ->
            if (person.mapOfPayments.isEmpty()) {
                addPaymentRowsToResultsTable(person, "-", "-")
                return@forEach
            }
            person.mapOfPayments.forEach { (key, value) ->
                addPaymentRowsToResultsTable(
                    person,
                    key.name,
                    ((value * 100.0).roundToInt() / 100.0).toString()
                )
            }
        }
        buttonPrevious.setOnClickListener {

            finish()

        }

    }

    /**
     * Add payment rows to results table
     * Method is responsible for adding new text views and table rows to table of results
     * @param person current person
     * @param wireToText who to wire money to
     * @param howMuchText how money to wire
     */
    private fun addPaymentRowsToResultsTable(
        person: Person,
        wireToText: String,
        howMuchText: String
    ) {
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
        wireToTextView.text = wireToText
        howMuchTextView.text = howMuchText

        tableRow.addView(passengerNameView)
        tableRow.addView(wireToTextView)
        tableRow.addView(howMuchTextView)

        tableOfPassengers?.addView(tableRow)
    }

    /**
     * Add passengers to list of payers or to be paid and sort it by how much passengers paid
     *
     */
    private fun addPassengersToListOfPayersOrToBePaid() {

        listOfPassengers?.forEach { person ->
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
    }
}