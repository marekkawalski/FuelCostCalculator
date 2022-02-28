package model

class FuelCostCalculator constructor(
    private var listOfDistances: ArrayList<Distance>,
    private var car: Car?
) {
    private var totalDistance: Double = 0.0

    fun calculateTotalFuelCost(): Double {
        for (i in listOfDistances) {
            totalDistance += i.distance
        }
        return (car?.avarageFuelConsumptions ?: 0.0) * (car?.costOfFuelLiter
            ?: 0.0) * totalDistance / 100
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
        val totalFuelCost = car?.totalFuelCost ?: 0 as Double
        for (i in listOfDistances) {
            totalDistance = i.distance
        }
        for (i in person.listOfPassengersSelectedDistances) {
            totalPersonCost += (totalFuelCost * i.distance) / (totalDistance * i.passengersCount)

        }
        return totalPersonCost
    }

}