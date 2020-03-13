package com.prismosis.checklist.utils

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

class DateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}