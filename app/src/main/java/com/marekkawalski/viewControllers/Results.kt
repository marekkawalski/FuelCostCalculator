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
import model.Person
import otherControllers.SettingsController
import kotlin.math.roundToInt

class Results : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_results)
        title = getString(R.string.app_full_name)

        val settings = SettingsController()
        settings.loadSettings(this, resources)

        val listOfPassengers = intent.getParcelableArrayListExtra<Person>("listOfPassengers")
        val tableOfPassengers = findViewById<TableLayout>(R.id.tableOfCostsLayout)
        val buttonPrevious = findViewById<ImageButton>(R.id.buttonPrevious)
        val mapOfPayments = mutableMapOf<Int, Double>()
        val mapOfDebts = mutableMapOf<Int, Double>()


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
                mapOfDebts[person.id] = person.costOfFuel
                mapOfPayments[person.id] = person.howMuchPaid
            }
            val sortedMapOfDebts = mapOfDebts.toList().sortedBy { (_, value) -> value }.toMap()
            val sortedMapOfPayments =
                mapOfPayments.toList().sortedBy { (_, value) -> value }.toMap()

            for (person in listOfPassengers) {
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
                howMuchTextView.text = ((person.costOfFuel * 100.0).roundToInt() / 100.0).toString()

                tableRow.addView(passengerNameView)
                tableRow.addView(wireToTextView)
                tableRow.addView(howMuchTextView)
                tableOfPassengers.addView(tableRow)
                Log.i("sorttest", person.howMuchPaid.toString())

            }
        }
        buttonPrevious.setOnClickListener {
            finish()
        }

    }
}