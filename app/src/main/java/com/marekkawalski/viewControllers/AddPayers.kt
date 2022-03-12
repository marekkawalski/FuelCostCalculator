package com.marekkawalski.viewControllers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.marekkawalski.fuelcostcalculator.R
import model.Car
import model.Person
import otherControllers.SettingsController

class AddPayers : AppCompatActivity() {
    private var payment: Double? = null
    private var listOfPayersViews = ArrayList<TableRow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = SettingsController()
        settings.loadSettings(this, resources)

        setContentView(R.layout.activity_add_payers)
        title = getString(R.string.app_full_name)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigationView)
        bottomNavigationView.selectedItemId = R.id.Car

        val listOfPassengers = intent.getParcelableArrayListExtra<Person>("listOfPassengers")
        val listOfCars = intent.getParcelableArrayListExtra<Car>("listOfCars")
        val payerSpinner = findViewById<Spinner>(R.id.payerSpinner)
        val carChooserSpinner = findViewById<Spinner>(R.id.carChooserSpinner)
        val addPaymentButton = findViewById<Button>(R.id.addPaymentButton)
        val deleteLastButton = findViewById<Button>(R.id.deletePaymentButton)
        val buttonPrevious = findViewById<ImageButton>(R.id.buttonPrevious)
        val buttonNext = findViewById<ImageButton>(R.id.buttonNext)
        val payerPriceInput = findViewById<TextInputEditText>(R.id.payerPriceInput)
        val payerPriceInputLayout = findViewById<TextInputLayout>(R.id.payerPriceInputLayout)
        val tableOfPayers = findViewById<TableLayout>(R.id.tableOfCostsLayout)
        var payerId = 0
        var carId = 0

        val listOfPassengersArray = ArrayList<String>()

        listOfPassengers?.forEach { person ->
            listOfPassengersArray.add(person.name)
        }

        val listOfCarsArray = ArrayList<String>()
        if (listOfCars != null) {
            for (car in listOfCars) {
                listOfCarsArray.add(car.carName + ", price: " + car.totalFuelCost)
            }
            listOfCarsArray.add("partial/multiple payments")
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOfCarsArray

        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            carChooserSpinner.adapter = adapter
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOfPassengersArray

        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            payerSpinner.adapter = adapter
        }

        payerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // get selected item text
                Log.i("test", parent.getItemAtPosition(position).toString())
                payerId = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }
        carChooserSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // get selected item text
                Log.i("test", parent.getItemAtPosition(position).toString())
                carId = position
                if (carId == listOfCars?.size) {
                    payerPriceInputLayout.visibility = VISIBLE

                } else {
                    payerPriceInputLayout.visibility = GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        //bottom navigation
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

        addPaymentButton.setOnClickListener {
            if (carId != listOfCars?.size) {
                payment = listOfCars?.get(carId)?.totalFuelCost ?: 0.0
                listOfPassengers?.get(payerId)?.listOfPayments?.add(
                    payment ?: return@setOnClickListener
                )

            } else {
                payment = payerPriceInput.text.toString().toDoubleOrNull()
                if (payment == null) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.providePayment),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                listOfPassengers?.get(payerId)?.listOfPayments?.add(
                    payment ?: return@setOnClickListener
                )

            }
            //add elements to UI
            val payerNameView = TextView(this)
            val paymentsTextView = TextView(this)
            val tableRow = TableRow(this)
            payerNameView.text = listOfPassengers?.get(payerId)?.name
            payerNameView.gravity = Gravity.CENTER
            payerNameView.width = ViewGroup.LayoutParams.WRAP_CONTENT

            paymentsTextView.gravity = Gravity.CENTER
            paymentsTextView.width = ViewGroup.LayoutParams.WRAP_CONTENT
            paymentsTextView.text = payment.toString()
            listOfPayersViews.add(tableRow)

            tableRow.addView(payerNameView)
            tableRow.addView(paymentsTextView)
            tableOfPayers.addView(tableRow)
        }

        deleteLastButton.setOnClickListener {
            if (listOfPassengers?.isNotEmpty() == true && listOfPayersViews.isNotEmpty()) {
                listOfPassengers[payerId]?.listOfPayments?.removeLast()

                tableOfPayers.removeView(listOfPayersViews.last())
                listOfPayersViews.removeLast()


            } else {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.nothing_to_delete),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonNext.setOnClickListener {
            var totalPassengersFuelCost = 0.0
            var actualTotalFuelCost = 0.0
            if (listOfPassengers != null && listOfCars != null) {
                for (person in listOfPassengers) {
                    for (payment in person.listOfPayments) {
                        person.howMuchPaid += payment
                    }
                    totalPassengersFuelCost += person.howMuchPaid
                }
                for (car in listOfCars) {
                    actualTotalFuelCost += car.totalFuelCost
                }

                if (actualTotalFuelCost != totalPassengersFuelCost) {
                    for (person in listOfPassengers) {
                        person.howMuchPaid = 0.0
                    }
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.fuelCostsAreNotEqual),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    val intent = Intent(this, Results::class.java)
                    intent.putExtra("listOfPassengers", listOfPassengers)
                    startActivity(intent)
                }

            }
        }
        //come back to previous activity
        buttonPrevious.setOnClickListener {
            finish()
        }
    }
    //@todo kiedy powrót z results do payment, problem z przyjsciem dalej
}