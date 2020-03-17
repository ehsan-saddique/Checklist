package com.prismosis.checklist.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.prismosis.checklist.utils.Enum
import com.prismosis.checklist.utils.toBooleanFromInt
import com.prismosis.checklist.utils.toDate
import com.prismosis.checklist.utils.toInt
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

@Entity (tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = false)
    var taskId: String,
    var parentId: String,
    var userId: String,
    var createdAt: Date,
    var taskName: String,
    var taskDescription: String?,
    var startDate: Date,
    var endDate: Date,
    var status: Enum.TaskStatus,
    var isDirty: Boolean = true,
    var isDeleted: Boolean = false
) {

}

data class DTOTask(var subTasksCount: Int?,
                         val taskId: String,
                         val parentId: String,
                        val userId: String,
                         val taskName: String,
                         var taskDescription: String?,
                         val createdAt: Date,
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
        parcel.readString(),
        createdAt = Date(parcel.readLong()),
        startDate = Date(parcel.readLong()),
        endDate = Date(parcel.readLong()),
        status = Enum.TaskStatus.getValueFromInt(parcel.readInt()),
        isDirty = parcel.readInt() == 1,
        isDeleted = parcel.readInt() == 1
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(subTasksCount ?: 0)
        parcel.writeString(taskId)
        parcel.writeString(parentId)
        parcel.writeString(userId)
        parcel.writeString(taskName)
        parcel.writeString(taskDescription)
        parcel.writeLong(createdAt.time)
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

        fun getTaskFromServerResponse(serverTask: TaskServerResponse): DTOTask {
            val status = Enum.TaskStatus.getValueFromInt(serverTask.status.integerValue.toInt())
            val task = DTOTask(0, serverTask.taskId.stringValue, serverTask.parentId.stringValue,
                serverTask.userId.stringValue, serverTask.taskName.stringValue, serverTask.taskDescription.stringValue,
                serverTask.createdAt.integerValue.toDate(), serverTask.startDate.integerValue.toDate(),
                serverTask.endDate.integerValue.toDate(), status, false,
                serverTask.isDeleted.integerValue.toBooleanFromInt())

            return task
        }
    }

    fun getTaskEntity(): Task {
        return Task(taskId, parentId, userId, createdAt, taskName, taskDescription, startDate, endDate, status, isDirty, isDeleted)
    }

    fun toServerResponse(): TaskServerResponse {
        val serverTask = TaskServerResponse()
        serverTask.taskId.stringValue = taskId
        serverTask.parentId.stringValue = parentId
        serverTask.userId.stringValue = userId
        serverTask.taskName.stringValue = taskName
        serverTask.taskDescription.stringValue = taskDescription ?: ""
        serverTask.createdAt.integerValue = createdAt.time.toString()
        serverTask.startDate.integerValue = startDate.time.toString()
        serverTask.endDate.integerValue = endDate.time.toString()
        serverTask.status.integerValue = status.value.toString()
        serverTask.isDirty.integerValue = "0"
        serverTask.isDeleted.integerValue = isDeleted.toInt().toString()

        return serverTask
    }

}