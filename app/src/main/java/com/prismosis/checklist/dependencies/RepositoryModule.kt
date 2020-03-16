package com.prismosis.checklist.dependencies

import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.networking.RestClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Ehsan Saddique on 2020-03-16
 */

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository = UserRepository()

    @Singleton
    @Provides
    fun provideTaskRepository(database: AppDatabase, restClient: RestClient): TaskRepository = TaskRepository(database, restClient)
}