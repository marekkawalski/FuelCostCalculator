package com.marekkawalski.controllers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

/**
 * This class allows user to add payments for fuel made during a trip.
 * @author Marek Kawalski
 * @version 1.4
 *
 */
class AddPayers : AppCompatActivity() {
    /**
     * @property payment amount of money paid by a passenger for fuel
     */
    private var payment: Double? = null

    /**
     * @property listOfPayersViews list of table rows with payer information
     */
    private var listOfPayersViews = ArrayList<TableRow>()

    /**
     * @property currentSum
     */
    private var currentSum = 0.0

    /**
     * @property listOfPassengers list of all passengers
     */
    private var listOfPassengers: ArrayList<Person>? = null

    /**
     * @property listOfCars list of all cars
     */
    private var listOfCars: ArrayList<Car>? = null

    /**
     * @property payerPriceInput input text where payer inputs how much money they paid
     */
    private var payerPriceInput: TextInputEditText? = null

    /**
     * @property payerPriceInputLayout layout of payerPriceInput
     */
    private var payerPriceInputLayout: TextInputLayout? = null

    /**
     * @property tableOfPayers table of payers
     */
    private var tableOfPayers: TableLayout? = null

    /**
     * @property paymentTextView text view where information about payment is displayed
     */
    private var paymentTextView: TextView? = null

    /**
     * @property payerId id of payer
     */
    private var payerId = 0

    /**
     * @property carId id of car
     */
    private var carId = 0

    /**
     * On create
     *
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = SettingsController()
        settings.loadSettings(this)

        setContentView(R.layout.activity_add_payers)
        title = getString(R.string.app_full_name)

        val bottomNavigationView: BottomNavigationView? = findViewById(R.id.bottom_navigationView)
        bottomNavigationView?.selectedItemId = R.id.Car

        val payerSpinner: Spinner? = findViewById(R.id.payerSpinner)
        val carChooserSpinner: Spinner? = findViewById(R.id.carChooserSpinner)
        val addPaymentButton: Button? = findViewById(R.id.addPaymentButton)
        val deleteLastButton: Button? = findViewById(R.id.deletePaymentButton)
        val buttonPrevious: ImageButton? = findViewById(R.id.buttonPrevious)
        val buttonNext: ImageButton? = findViewById(R.id.buttonNext)

        listOfPassengers = intent.getParcelableArrayListExtra("listOfPassengers")
        listOfCars = intent.getParcelableArrayListExtra("listOfCars")

        payerPriceInput = findViewById(R.id.payerPriceInput)
        payerPriceInputLayout = findViewById(R.id.payerPriceInputLayout)
        tableOfPayers = findViewById(R.id.tableOfCostsLayout)
        paymentTextView = findViewById(R.id.paymentTextView)
        payerId = 0
        carId = 0

        val listOfPassengersArray = ArrayList<String>()

        listOfPassengers?.forEach { person ->
            listOfPassengersArray.add(person.name)
        }

        val listOfCarsArray = ArrayList<String>()

        listOfCars?.forEach { car ->
            listOfCarsArray.add(car.carName + ", " + resources.getString(R.string.price) + " " + car.totalFuelCost)
        }
        listOfCarsArray.add(resources.getString(R.string.partialPayment))

        createAnArrayAdapter(carChooserSpinner, listOfCarsArray)

        createAnArrayAdapter(payerSpinner, listOfPassengersArray)

        payerSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // get selected item text
                payerId = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }
        carChooserSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // get selected item text
                carId = position
                if (carId == listOfCars?.size) {
                    payerPriceInputLayout?.visibility = VISIBLE

                } else {
                    payerPriceInputLayout?.visibility = GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        //bottom navigation
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

        addPaymentButton?.setOnClickListener {

            handleAddingPayment()
        }

        deleteLastButton?.setOnClickListener {

            handleDeletingLastPayment()
        }

        buttonNext?.setOnClickListener {

            handleMovingToNextActivity()
        }
        //come back to previous activity
        buttonPrevious?.setOnClickListener {
            finish()
        }
    }

    /**
     * Method handles adding new payment.
     *
     */
    private fun handleAddingPayment() {
        if (carId != listOfCars?.size) {
            payment = listOfCars?.get(carId)?.totalFuelCost ?: 0.0
            listOfPassengers?.get(payerId)?.listOfPayments?.add(
                payment ?: return
            )

        } else {
            payment = payerPriceInput?.text.toString().toDoubleOrNull()
            if (payment == null) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.providePayment),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            if (payment ?: 0.0 <= 0.0) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.numberMustBePositive),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return
            }
            listOfPassengers?.get(payerId)?.listOfPayments?.add(
                payment ?: return
            )

        }
        addPaymentToUi()
    }

    /**
     * Method adds payment to UI.
     *
     */
    @SuppressLint("SetTextI18n")
    private fun addPaymentToUi() {
        val payerNameView = TextView(this)
        val paymentsTextView = TextView(this)
        val tableRow = TableRow(this)
        payerNameView.text = listOfPassengers?.get(payerId)?.name
        payerNameView.gravity = Gravity.CENTER
        payerNameView.width = ViewGroup.LayoutParams.WRAP_CONTENT

        paymentsTextView.gravity = Gravity.CENTER
        paymentsTextView.width = ViewGroup.LayoutParams.WRAP_CONTENT
        paymentsTextView.text = payment.toString()
        currentSum += payment ?: 0.0
        paymentTextView?.text =
            resources.getString(R.string.payment) + " (" + currentSum.toString() + ")"
        listOfPayersViews.add(tableRow)

        tableRow.addView(payerNameView)
        tableRow.addView(paymentsTextView)
        tableOfPayers?.addView(tableRow)
    }

    /**
     * Method handles deleting last payment from the payment list.
     *
     */
    @SuppressLint("SetTextI18n")
    private fun handleDeletingLastPayment() {
        if (listOfPassengers?.isNotEmpty() == true && (listOfPassengers
                ?: return)[payerId].listOfPayments.isNotEmpty()
        ) {
            currentSum -= (listOfPassengers
                ?: return)[payerId].listOfPayments.last()
            (listOfPassengers
                ?: return)[payerId].listOfPayments.removeLast()
            paymentTextView?.text =
                resources.getString(R.string.payment) + " (" + currentSum.toString() + ")"

            if (listOfPayersViews.isNotEmpty()) {
                tableOfPayers?.removeView(listOfPayersViews.last())
                listOfPayersViews.removeLast()
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.paymentRemoved),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.nothing_to_delete),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Method handles moving to next activity.
     *
     */
    private fun handleMovingToNextActivity() {
        var totalPassengersFuelCost = 0.0
        var actualTotalFuelCost = 0.0

        listOfPassengers?.forEach { person ->
            person.howMuchPaid = 0.0
        }
        if (listOfPassengers != null && listOfCars != null) {

            listOfPassengers?.forEach { person ->
                person.listOfPayments.forEach { payment ->
                    person.howMuchPaid += payment
                }
                totalPassengersFuelCost += person.howMuchPaid
            }
            listOfCars?.forEach { car ->
                actualTotalFuelCost += car.totalFuelCost
            }

            if (actualTotalFuelCost != totalPassengersFuelCost) {

                listOfPassengers?.forEach { person ->
                    person.howMuchPaid = 0.0
                }
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.fuelCostsAreNotEqual),
                    Toast.LENGTH_SHORT
                ).show()
                return
            } else {
                val intent = Intent(this, Results::class.java)
                intent.putExtra("listOfPassengers", listOfPassengers)
                startActivity(intent)
            }
        }
    }

    /**
     * Method creates array adapters, spinners
     *
     * @param spinner spinner
     * @param listOfChoice list from which user can choose payments
     */
    private fun createAnArrayAdapter(spinner: Spinner?, listOfChoice: ArrayList<String>) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOfChoice

        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner?.adapter = adapter
        }
    }
}