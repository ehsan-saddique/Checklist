package com.prismosis.checklist.utils

import android.app.Application
import android.content.Context

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

class ChecklistApplication: Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: ChecklistApplication? = null

        fun context() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

}