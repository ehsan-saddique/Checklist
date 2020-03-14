package com.prismosis.checklist.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.prismosis.checklist.utils.DateTimeConverter
import java.util.*

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

@Entity (tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: String,
    val parentId: String,
    val name: String,
    var description: String?,
    var startDate: Date,
    var endDate: Date,
    var isDelete: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        startDate = Date(parcel.readLong()),
        endDate = Date(parcel.readLong()),
        isDelete = parcel.readInt() == 1
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(parentId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeLong(startDate.time)
        parcel.writeLong(endDate.time)
        parcel.writeInt(if (isDelete) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}