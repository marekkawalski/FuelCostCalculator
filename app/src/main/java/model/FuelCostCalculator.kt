package model

class FuelCostCalculator constructor(
    private var listOfDistances: ArrayList<Distance?>,
    private var car: Car?
) {

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
        for (i in listOfDistances) {
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