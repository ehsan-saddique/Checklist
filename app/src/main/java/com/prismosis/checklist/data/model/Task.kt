package com.prismosis.checklist.data.model

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
    var endDate: Date
)