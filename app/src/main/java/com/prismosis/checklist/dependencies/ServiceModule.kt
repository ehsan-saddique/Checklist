package com.prismosis.checklist.dependencies

import com.google.firebase.auth.FirebaseAuth
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.networking.RestClient
import com.prismosis.checklist.ui.authentication.login.LoginViewModel
import com.prismosis.checklist.ui.task.TaskViewModel
import com.prismosis.checklist.utils.Utils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Ehsan Saddique on 2020-03-16
 */

@Module
class ServiceModule {
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase = AppDatabase.getAppDataBase()

    @Provides
    @Singleton
    fun provideRestClient(): RestClient = RestClient()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideUtils(): Utils = Utils.instance
}