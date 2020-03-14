package com.prismosis.checklist.utils

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

class TypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTaskStatus(value: Int?): Enum.TaskStatus? {
        return if (value == null) null else Enum.TaskStatus.getValueFromInt(value)
    }

    @TypeConverter
    fun taskStatusToInt(taskStatus: Enum.TaskStatus?): Int? {
        return taskStatus?.value
    }

}