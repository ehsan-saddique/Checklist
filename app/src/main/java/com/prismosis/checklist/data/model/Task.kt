package com.prismosis.checklist.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.prismosis.checklist.utils.Enum
import java.util.*

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

@Entity (tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val id: String,
    val parentId: String,
    val name: String,
    var description: String?,
    var startDate: Date,
    var endDate: Date,
    var status: Enum.TaskStatus,
    var isDirty: Boolean = true,
    var isDeleted: Boolean = false
) {

}

//data class DTOTask(@Embedded var task: Task, var subTasksCount: Int) {
//
//}

data class DTOTask(var subTasksCount: Int,
                         val id: String,
                         val parentId: String,
                         val name: String,
                         var description: String?,
                         var startDate: Date,
                         var endDate: Date,
                         var status: Enum.TaskStatus,
                         var isDirty: Boolean = true,
                         var isDeleted: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        startDate = Date(parcel.readLong()),
        endDate = Date(parcel.readLong()),
        status = Enum.TaskStatus.getValueFromInt(parcel.readInt()),
        isDirty = parcel.readInt() == 1,
        isDeleted = parcel.readInt() == 1
    ) {
    }

    fun getTaskEntity(): Task {
        return Task(0, id, parentId, name, description, startDate, endDate, status, isDirty, isDeleted)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(subTasksCount)
        parcel.writeString(id)
        parcel.writeString(parentId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeLong(startDate.time)
        parcel.writeLong(endDate.time)
        parcel.writeInt(status.value)
        parcel.writeInt(if (isDirty) 1 else 0)
        parcel.writeInt(if (isDeleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DTOTask> {
        override fun createFromParcel(parcel: Parcel): DTOTask {
            return DTOTask(parcel)
        }

        override fun newArray(size: Int): Array<DTOTask?> {
            return arrayOfNulls(size)
        }
    }

}