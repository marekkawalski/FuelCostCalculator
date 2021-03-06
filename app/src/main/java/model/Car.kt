package model

import android.os.Parcel
import android.os.Parcelable

/**
 * Class represents a car that took part in a trip
 * @author Marek Kawalski
 * @version 1.3
 */
class Car(
    /**
     *@property carName name of the car
     */
    var carName: String = ""
) : Parcelable {

    /**
     * @property totalFuelCost total fuel cost
     */
    var totalFuelCost: Double = 0.0

    /**
     * @property totalDistance total distance covered by the car
     */
    var totalDistance: Double = 0.0

    /**
     * @property averageFuelConsumptions car average fuel consumption
     */
    var averageFuelConsumptions: Double = 0.0

    /**
     * @property costOfFuelLiter cost of fuel liter (fuel that was used by current car)
     */
    var costOfFuelLiter: Double = 0.0

    constructor(
        carName: String,
        costOfFuelLiter: Double,
        averageFuelConsumptions: Double,
    ) : this() {
        this.carName = carName
        this.costOfFuelLiter = costOfFuelLiter
        this.averageFuelConsumptions = averageFuelConsumptions
    }

    constructor(
        carName: String,
        totalFuelCost: Double,
    ) : this() {
        this.carName = carName
        this.totalFuelCost = totalFuelCost
    }


    override fun describeContents(): Int {
        return 0
    }

    constructor(parcelIn: Parcel) : this() {
        carName = parcelIn.readString() as String
        totalDistance = parcelIn.readDouble()
        totalFuelCost = parcelIn.readDouble()
        averageFuelConsumptions = parcelIn.readDouble()
        costOfFuelLiter = parcelIn.readDouble()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeString(carName)
        out.writeDouble(totalDistance)
        out.writeDouble(totalFuelCost)
        out.writeDouble(averageFuelConsumptions)
        out.writeDouble(costOfFuelLiter)
    }

    companion object CREATOR : Parcelable.Creator<Car?> {
        override fun createFromParcel(`in`: Parcel): Car {
            return Car(`in`)
        }

        override fun newArray(size: Int): Array<Car?> {
            return arrayOfNulls(size)
        }
    }
}