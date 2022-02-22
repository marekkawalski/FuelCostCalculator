package model

import android.os.Parcel
import android.os.Parcelable

class Distance : Parcelable {
    var distanceName: String = ""
    var distance: Double = 0.0

    constructor(distanceName: String, distance: Double) {
        this.distanceName = distanceName
        this.distance = distance
    }

    override fun describeContents(): Int {
        return 0
    }

    private constructor(parcelIn: Parcel) {
        distanceName = parcelIn.readString() as String
        distance = parcelIn.readDouble()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeString(distanceName)
        out.writeDouble(distance)
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