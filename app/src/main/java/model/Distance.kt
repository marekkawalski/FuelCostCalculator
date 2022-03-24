package model

import android.os.Parcel
import android.os.Parcelable

/**
 * Class represents particular distance covered during a trip (for instance a distance to London from Paris)
 * @author Marek Kawalski
 * @version 1.3
 */
class Distance : Parcelable {
    /**
     *@property distanceName name of the distance for instance: "to Paris" or "back to London"
     */
    var distanceName: String = ""

    /**
     *@property distance distance length for instance: 50km or 100 miles
     */
    var distance: Double = 0.0

    /**
     *@property passengersCount count of passengers present on particular distance
     */
    var passengersCount: Int = 0

    /**
     *@property listOfCars list of cars that covered that distance for instance two cars covered a distance from London toParis, so the list contains two cars
     */
    var listOfCars = ArrayList<Car?>()

    /**
     *@property id car Id, used so as not to compare car names
     */
    var id: Int

    constructor(distanceName: String, distance: Double, id: Int) {
        this.distanceName = distanceName
        this.distance = distance
        this.id = id
    }

    constructor(
        distanceName: String,
        distance: Double,
        passengersCount: Int,
        listOfCars: ArrayList<Car?>,
        id: Int
    ) {
        this.distanceName = distanceName
        this.distance = distance
        this.passengersCount = passengersCount
        this.listOfCars = listOfCars
        this.id = id
    }

    override fun describeContents(): Int {
        return 0
    }

    private constructor(parcelIn: Parcel) {
        distanceName = parcelIn.readString() as String
        distance = parcelIn.readDouble()
        passengersCount = parcelIn.readInt()
        id = parcelIn.readInt()
        parcelIn.readTypedList(this.listOfCars, Car.CREATOR)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeString(distanceName)
        out.writeDouble(distance)
        out.writeInt(passengersCount)
        id.let { out.writeInt(it) }
        out.writeTypedList(this.listOfCars)
    }

    companion object CREATOR : Parcelable.Creator<Distance?> {
        override fun createFromParcel(`in`: Parcel): Distance {
            return Distance(`in`)
        }

        override fun newArray(size: Int): Array<Distance?> {
            return arrayOfNulls(size)
        }
    }

}