package com.prismosis.checklist.utils

import android.app.job.JobParameters
import android.app.job.JobService
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.prismosis.checklist.data.Result
import com.prismosis.checklist.data.repositories.TaskRepository
import javax.inject.Inject

/**
 * Created by Ehsan Saddique on 2020-03-18
 */
class ScheduleService : JobService() {

    var mTaskRepository = ChecklistApplication.instance?.appComponent?.getTaskRepository()

    override fun onStartJob(params: JobParameters?): Boolean {

        if (mTaskRepository == null || FirebaseAuth.getInstance().currentUser == null) {
            jobFinished(params, true)
            return false
        }
        else {
            mTaskRepository!!.uploadTasksToCloud { result ->
                if (result is Result.Success) {
                    println("Data has been synced through background service")
                }
                else {
                    println("Error syncing data through background service")
                }
                jobFinished(params, true)
            }

            return true
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {

        return true
    }
}