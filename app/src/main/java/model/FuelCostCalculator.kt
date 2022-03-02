package model

class FuelCostCalculator(
    private var listOfDistances: ArrayList<Distance?>?,
    private var car: Car?
) {

    private fun calculateTotalDistance(): Double {
        var totalDistance = 0.0
        for (i in listOfDistances ?: return 0.0) {
            totalDistance += i?.distance ?: 0.0
        }
        return totalDistance
    }

    fun calculateTotalCostOfFuel(): Double {
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
        var totalDistance = 0.0
        val totalFuelCost: Double = car?.totalFuelCost ?: 0.0
        for (i in listOfDistances ?: return 0.0) {
            if (i != null) {
                totalDistance += i.distance
            }
        }
        for (i in person.listOfPassengersSelectedDistances) {
            totalPersonCost += ((totalFuelCost * i.distance) / (totalDistance * i.passengersCount
                    ))
        }
        return totalPersonCost
    }

}