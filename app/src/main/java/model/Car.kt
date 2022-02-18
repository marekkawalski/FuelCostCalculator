package model

class Car constructor(carName: String, totalDistance: Double) {
     val carName: String = carName
     val totalDistance: Double = totalDistance
     var totalFuelCost: Double = 0.0
     var avarageFuelConsumptions: Double = 0.0
     var costOfFuelLiter: Double = 0.0
}