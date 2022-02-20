package model

class Distance(distanceName: String, distance: Double) {
    var distanceName: String = ""
    var distance: Double = 0.0

    init {
        this.distanceName = distanceName
        this.distance = distance
    }
}