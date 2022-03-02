package model

import android.os.Parcel
import android.os.Parcelable

class Car : Parcelable {
    var carName: String = ""
    var totalFuelCost: Double = 0.0
    var totalDistance: Double = 0.0
    var averageFuelConsumptions: Double = 0.0
    var costOfFuelLiter: Double = 0.0


    constructor(carName: String) {
        this.carName = carName
    }

    override fun describeContents(): Int {
        return 0
    }

    private constructor(parcelIn: Parcel) {
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