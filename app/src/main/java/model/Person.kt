package model

import android.os.Parcel
import android.os.Parcelable

/**
 * Class represents a passenger who took part in a trip
 * @author Marek Kawalski
 * @version 1.3
 */
class Person : Parcelable {
    /**
     *@property name name of passenger
     */
    var name: String = ""

    /**
     *@property id passenger id
     */
    var id: Int

    /**
     *@property coveredDistance total covered distance by a particular person
     */
    var coveredDistance: Double = 0.0

    /**
     *@property costOfFuel what passenger is to pay for their trip
     */
    var costOfFuel: Double = 0.0

    /**
     * @property listOfPassengersSelectedDistances list of distances in which a passenger took part
     */
    var listOfPassengersSelectedDistances = ArrayList<Distance>()

    /**
     * @property howMuchPaid how much a passenger paid minus costOfFuel, can be a negative number
     */
    var howMuchPaid: Double = 0.0

    /**
     *@property listOfPayments list of payments made by a particular passenger
     */
    var listOfPayments = ArrayList<Double>()

    /**
     *@property mapOfPayments map of payments to be made by particular passengers, it contains who to wire money to and how much to wire
     */
    var mapOfPayments = mutableMapOf<Person, Double>()

    constructor(name: String, id: Int) {
        this.name = name
        this.id = id
    }

    constructor(name: String, listOfPassengersSelectedDistances: ArrayList<Distance>, id: Int) {
        this.name = name
        this.listOfPassengersSelectedDistances = listOfPassengersSelectedDistances
        this.id = id
    }

    private constructor(parcelIn: Parcel) {
        name = parcelIn.readString() as String
        id = parcelIn.readInt()
        coveredDistance = parcelIn.readDouble()
        costOfFuel = parcelIn.readDouble()
        howMuchPaid = parcelIn.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        id.let { parcel.writeInt(it) }
        parcel.writeDouble(coveredDistance)
        parcel.writeDouble(costOfFuel)
        parcel.writeDouble(howMuchPaid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Person> {
        override fun createFromParcel(parcel: Parcel): Person {
            return Person(parcel)
        }

        override fun newArray(size: Int): Array<Person?> {
            return arrayOfNulls(size)
        }
    }
}

