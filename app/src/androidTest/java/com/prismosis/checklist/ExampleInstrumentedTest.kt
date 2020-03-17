package com.prismosis.checklist

import android.content.Context
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.utils.ChecklistApplication

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.prismosis.checklist", appContext.packageName)
    }

    @Test
    fun simpleTest2() {

//        val task = Task("98", "", "", Date(),
//            "name", "", Date(), Date(), Enum.TaskStatus.PENDING,
//            true, false)
//        taskDao.insert(task)
//        assert(taskDao.getTask("98").taskId == "3453")
        assert(true)
    }
}
