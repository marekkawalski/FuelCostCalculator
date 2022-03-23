package model

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
    var listOfDistances: ArrayList<Distance?>? = null,
    var listOfCars: ArrayList<Car?>? = null
) {

    /**
     * Calculate total distance covered by all cars
     *
     * @return total distance covered by all cars
     */
    private fun calculateTotalDistance(): Double {
        var totalDistance = 0.0

        listOfDistances?.forEach { distance ->
            distance?.listOfCars?.forEach { _ ->
                totalDistance += distance.distance
            }
        }
        return totalDistance
    }

    /**
     * Calculate total cost of fuel
     * Method calculates car total fuel cost based on car average consumption, cost of fuel liter and total distance
     * covered by the car
     */
    fun calculateTotalCostOfFuel() {
        listOfCars?.forEach { car ->
            if (car?.totalFuelCost == 0.0) {
                car.totalFuelCost =
                    car.averageFuelConsumptions * car.costOfFuelLiter * calculateTotalDistance() / 100
            }
        }
    }

    /**
     * Calculate passenger total distance
     *
     * @param person person whose distance is to be calculated
     * @return total distance covered by given person
     */
    fun calculatePassengerTotalDistance(person: Person): Double {
        var coveredDistance = 0.0
        person.listOfPassengersSelectedDistances.forEach { distance ->
            coveredDistance += distance.distance
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
            if (totalDistance == 0.0 || distance.passengersCount == 0) {
                throw FuelCostCalculatorException(
                    "Division by zero is impossible!!"
                )
            }
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
                        listOfPayers[i].mapOfPayments[personToBePaid] =
                            listOfPayers[i].howMuchPaid
                        listOfPayers[i].howMuchPaid = 0.0

                    } else {
                        listOfPayers[i].howMuchPaid -= tempAmount
                        listOfPayers[i].mapOfPayments[personToBePaid] = tempAmount
                        tempAmount = 0.0
                    }
                }
                i++
            }
        }
    }
}