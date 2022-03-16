package model

import android.os.Parcel
import android.os.Parcelable

/**
 * Car
 * Class represents a car that took part in a trip
 * @author Marek Kawalski
 */
class Car : Parcelable {
    /**
     *@property carName of the car
     */
    var carName: String = ""

    /**
     * @property totalFuelCost Total fuel cost
     */
    var totalFuelCost: Double = 0.0

    /**
     * @property totalDistance total distance covered by the car
     */
    private var totalDistance: Double = 0.0

    /**
     * @property averageFuelConsumptions car average fuel consumption
     */
    var averageFuelConsumptions: Double = 0.0

    /**
     * @property costOfFuelLiter cost of fuel liter (fuel that was used by current car)
     */
    var costOfFuelLiter: Double = 0.0


    constructor(carName: String) {
        this.carName = carName
    }

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcelIn: Parcel) {
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