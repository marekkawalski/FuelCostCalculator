package model

class FuelCostCalculator(
    private var listOfDistances: ArrayList<Distance?>?,
    private var listOfCars: ArrayList<Car?>
) {

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

    fun calculateTotalCostOfFuel(car: Car?): Double {
        return (car?.averageFuelConsumptions ?: 0.0) * (car?.costOfFuelLiter
            ?: 0.0) * calculateTotalDistance() / 100
    }

    fun calculatePassengerTotalDistance(person: Person): Double {
        var coveredDistance = 0.0
        for (i in person.listOfPassengersSelectedDistances) {
            coveredDistance += i.distance
        }
        return coveredDistance
    }

    fun calculatePassengerTotalCost(person: Person): Double {
        var totalPersonCost = 0.0
        val totalDistance = calculateTotalDistance()
        var totalFuelCost = 0.0
        for (car in listOfCars) {
            totalFuelCost += car?.totalFuelCost ?: 0.0
        }

        for (distance in person.listOfPassengersSelectedDistances) {
            totalPersonCost += ((totalFuelCost * distance.distance * distance.listOfCars.count()) / (totalDistance * distance.passengersCount
                    ))
        }
        return totalPersonCost
    }

}