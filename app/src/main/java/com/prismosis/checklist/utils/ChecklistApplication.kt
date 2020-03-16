package com.prismosis.checklist.utils

import android.app.Application
import android.content.Context
import com.prismosis.checklist.dependencies.AppComponent
import com.prismosis.checklist.dependencies.DaggerAppComponent

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

class ChecklistApplication: Application() {
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
    }

}