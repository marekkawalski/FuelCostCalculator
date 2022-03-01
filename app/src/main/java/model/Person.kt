package model

import android.os.Parcel
import android.os.Parcelable

class Person : Parcelable {
    var name: String = ""
    var id: Int
    var coveredDistance: Double = 0.0
    var costOfFuel: Double = 0.0
    var listOfPassengersSelectedDistances = ArrayList<Distance>()

    constructor(name: String, id: Int) {
        this.name = name
        this.id = id
    }

    private constructor(parcelIn: Parcel) {
        name = parcelIn.readString() as String
        id = parcelIn.readInt()
        coveredDistance = parcelIn.readDouble()
        costOfFuel = parcelIn.readDouble()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        id.let { parcel.writeInt(it) }
        parcel.writeDouble(coveredDistance)
        parcel.writeDouble(costOfFuel)
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

