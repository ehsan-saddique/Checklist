package com.prismosis.checklist.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.data.model.TaskDao
import com.prismosis.checklist.utils.ChecklistApplication
import com.prismosis.checklist.utils.DateTimeConverter

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(): AppDatabase {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(ChecklistApplication.context(), AppDatabase::class.java, "checklist_database.db").build()
                }
            }
            return INSTANCE!!
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }

}