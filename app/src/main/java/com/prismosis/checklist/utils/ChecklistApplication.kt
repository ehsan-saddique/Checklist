package com.prismosis.checklist.utils

import android.app.Application
import android.content.Context
import com.prismosis.checklist.dependencies.AppComponent
import com.prismosis.checklist.dependencies.DaggerAppComponent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.util.Log


/**
 * Created by Ehsan Saddique on 2020-03-13
 */

class ChecklistApplication: Application() {
    private val JOB_ID = 999
    var appComponent: AppComponent = DaggerAppComponent.create()
    init {
        instance = this
    }

    companion object {
        var instance: ChecklistApplication? = null

        fun context() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (!isJobServiceOn()) {
            val componentName = ComponentName(this, ScheduleService::class.java)

            val mJobInfo: JobInfo

            mJobInfo = JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build()


            val mScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val resultCode = mScheduler.schedule(mJobInfo)

            if (resultCode == JobScheduler.RESULT_SUCCESS)
                Log.e("SYNC SCHEDULER SERVICE", "Sync Scheduled")
            else
                Log.e("SYNC SCHEDULER SERVICE", "Sync not Scheduled")
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        val mScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        mScheduler.cancel(JOB_ID)
    }

    private fun isJobServiceOn(): Boolean {
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        var hasBeenScheduled = false

        for (jobInfo in scheduler.allPendingJobs) {
            if (jobInfo.id == JOB_ID) {
                hasBeenScheduled = true
                break
            }
        }

        return hasBeenScheduled
    }

}