package model

import android.util.Log

/**
 * Fuel cost calculator
 * Class which handles all calculations
 *
 * @property listOfDistances list of distances covered by a passenger
 * @property listOfCars list of cars that took part in the trip
 * @constructor Create empty Fuel cost calculator
 * @author Marek Kawalski
 */
class FuelCostCalculator(
    private var listOfDistances: ArrayList<Distance?>? = null,
    private var listOfCars: ArrayList<Car?>? = null
) {

    /**
     * Calculate total distance covered by all cars
     *
     * @return total distance covered by all cars
     */
    private fun calculateTotalDistance(): Double {
        var totalDistance = 0.0
        for (distance in listOfDistances ?: return 0.0) {
            if (distance != null) {
                for (j in distance.listOfCars) {
                    totalDistance += distance.distance
                }
            }
        }
        return totalDistance
    }

    /**
     * Calculate total cost of fuel
     * Method calculates car total fuel cost based on car average consumption, cost of fuel liter and total distance
     * covered by the car
     * @param car car which fuel cost is to be calculated
     * @return cost of fuel
     */
    fun calculateTotalCostOfFuel(car: Car?): Double {
        return (car?.averageFuelConsumptions ?: 0.0) * (car?.costOfFuelLiter
            ?: 0.0) * calculateTotalDistance() / 100
    }

    /**
     * Calculate passenger total distance
     *
     * @param person person whose distance is to be calculated
     * @return total distance covered by given person
     */
    fun calculatePassengerTotalDistance(person: Person): Double {
        var coveredDistance = 0.0
        for (i in person.listOfPassengersSelectedDistances) {
            coveredDistance += i.distance
        }
        return coveredDistance
    }

    /**
     * Calculate passenger total cost
     *
     * @param person person whose total cost is to be calculated
     * @return passenger total cost, how much trip cost them
     */
    fun calculatePassengerTotalCost(person: Person): Double {
        var totalPersonCost = 0.0
        val totalDistance = calculateTotalDistance()
        var totalFuelCost = 0.0
        listOfCars?.forEach { car ->
            totalFuelCost += car?.totalFuelCost ?: 0.0
        }

        for (distance in person.listOfPassengersSelectedDistances) {
            totalPersonCost += ((totalFuelCost * distance.distance * distance.listOfCars.count()) / (totalDistance * distance.passengersCount
                    ))
        }
        return totalPersonCost
    }

    /**
     * Who passenger should pay
     *
     * @param listOfPayers list of people who owe for a trip (all of their payments minus their total trip cost is negative)
     * @param listOfPeopleToBePaid list of people who are to be repaid (all of their payments minus their total trip cost is positive)
     */
    fun whoPassengerShouldPay(
        listOfPayers: ArrayList<Person>,
        listOfPeopleToBePaid: ArrayList<Person>
    ) {

        for (payer in listOfPayers) {
            payer.howMuchPaid *= -1
        }
        var tempAmount: Double
        var i: Int
        for (personToBePaid in listOfPeopleToBePaid) {
            tempAmount = personToBePaid.howMuchPaid
            i = 0
            while (tempAmount != 0.0 && i != listOfPayers.size) {

                if (listOfPayers[i].howMuchPaid != 0.0) {

                    if (tempAmount >= listOfPayers[i].howMuchPaid) {
                        tempAmount -= listOfPayers[i].howMuchPaid
                        listOfPayers[i].mapOfPayments[personToBePaid] = listOfPayers[i].howMuchPaid
                        listOfPayers[i].howMuchPaid = 0.0

                    } else {
                        listOfPayers[i].howMuchPaid -= tempAmount
                        listOfPayers[i].mapOfPayments[personToBePaid] = tempAmount
                        tempAmount = 0.0
                    }

                    Log.i(
                        "sorted",
                        "personToBePaid: " + personToBePaid.name + " who pays: " + listOfPayers[i].name + " amount: " +
                                listOfPayers[i].mapOfPayments[personToBePaid].toString() + "\n"
                    )
                }
                i++
            }
        }
    }
}