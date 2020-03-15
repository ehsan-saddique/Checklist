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
    val taskId: String,
    val parentId: String,
    val userId: String,
    val createdAt: Date,
    val taskName: String,
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

        fun getTaskFromParameters(parameters: Map<String, Any>): DTOTask {
            val STRING_TYPE = "stringValue"
            val INTEGER_TYPE = "integerValue"

            val taskIdMap    = (parameters.get("taskId")    as? Map<String, String>)?.get(STRING_TYPE) ?: ""
            val parentIdMap  = (parameters.get("parentId")  as? Map<String, String>)?.get(STRING_TYPE) ?: ""
            val userIdMap    = (parameters.get("userId")    as? Map<String, String>)?.get(STRING_TYPE) ?: ""
            val taskNameMap  = (parameters.get("taskName")  as? Map<String, String>)?.get(STRING_TYPE) ?: ""
            val createdAtMap = (parameters.get("createdAt") as? Map<String, String>)?.get(INTEGER_TYPE) ?: ""
            val startDateMap = (parameters.get("startDate") as? Map<String, String>)?.get(INTEGER_TYPE) ?: ""
            val endDateMap   = (parameters.get("endDate")   as? Map<String, String>)?.get(INTEGER_TYPE) ?: ""
            val statusMap    = (parameters.get("status")    as? Map<String, String>)?.get(INTEGER_TYPE) ?: ""
            val isDirtyMap   = (parameters.get("isDirty")   as? Map<String, String>)?.get(INTEGER_TYPE) ?: ""
            val isDeletedMap = (parameters.get("isDeleted") as? Map<String, String>)?.get(INTEGER_TYPE) ?: ""
            val taskDescriptionMap = (parameters.get("taskDescription") as? Map<String, String>)?.get(STRING_TYPE) ?: ""

            val task = DTOTask(0, taskIdMap, parentIdMap,
                userIdMap, taskNameMap, taskDescriptionMap,
                createdAtMap.toDate(), startDateMap.toDate(),
                endDateMap.toDate(), Enum.TaskStatus.getValueFromInt(statusMap.toInt()),
                false, isDeletedMap.toBooleanFromInt())

            return task
        }
    }

    fun getTaskEntity(): Task {
        return Task(taskId, parentId, userId, createdAt, taskName, taskDescription, startDate, endDate, status, isDirty, isDeleted)
    }

    fun getTaskParameters(): HashMap<String, Any> {
        val STRING_TYPE = "stringValue"
        val INTEGER_TYPE = "integerValue"

        val parameters = HashMap<String, Any>()
        val taskIdMap: HashMap<String, String> = hashMapOf    (STRING_TYPE  to  taskId)
        val parentIdMap: HashMap<String, String> = hashMapOf  (STRING_TYPE  to  parentId)
        val userIdMap: HashMap<String, String> = hashMapOf    (STRING_TYPE  to  userId)
        val taskNameMap: HashMap<String, String> = hashMapOf  (STRING_TYPE  to  taskName)
        val createdAtMap: HashMap<String, String> = hashMapOf (INTEGER_TYPE to  createdAt.time.toString())
        val startDateMap: HashMap<String, String> = hashMapOf (INTEGER_TYPE to  startDate.time.toString())
        val endDateMap: HashMap<String, String> = hashMapOf   (INTEGER_TYPE to  endDate.time.toString())
        val statusMap: HashMap<String, String> = hashMapOf    (INTEGER_TYPE to  status.value.toString())
        val isDirtyMap: HashMap<String, String> = hashMapOf   (INTEGER_TYPE to  "0")
        val isDeletedMap: HashMap<String, String> = hashMapOf (INTEGER_TYPE to  isDeleted.toInt().toString())
        val taskDescriptionMap: HashMap<String, String> = hashMapOf (STRING_TYPE to (taskDescription ?: ""))

        parameters["taskId"] = taskIdMap
        parameters["parentId"] = parentIdMap
        parameters["userId"] = userIdMap
        parameters["taskName"] = taskNameMap
        parameters["createdAt"] = createdAtMap
        parameters["startDate"] = startDateMap
        parameters["endDate"] = endDateMap
        parameters["status"] = statusMap
        parameters["isDirty"] = isDirtyMap
        parameters["isDeleted"] = isDeletedMap
        parameters["taskDescription"] = taskDescriptionMap

        val finalParameters = HashMap<String, Any>()
        finalParameters["fields"] = parameters

        return finalParameters

    }
}