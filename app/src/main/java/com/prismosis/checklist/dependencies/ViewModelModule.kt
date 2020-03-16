package com.prismosis.checklist.dependencies

import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.ui.authentication.login.LoginViewModel
import com.prismosis.checklist.ui.authentication.phone.PhoneAuthViewModel
import com.prismosis.checklist.ui.authentication.signup.SignupViewModel
import com.prismosis.checklist.ui.task.TaskUpdateViewModel
import com.prismosis.checklist.ui.task.TaskViewModel
import com.prismosis.checklist.ui.taskdetail.TaskDetailViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Ehsan Saddique on 2020-03-16
 */

@Module
class ViewModelModule {
    @Provides
    @Singleton
    fun provideLoginViewModel(userRepository: UserRepository): LoginViewModel = LoginViewModel(userRepository)

    @Provides
    @Singleton
    fun provideSignupViewModel(userRepository: UserRepository): SignupViewModel = SignupViewModel(userRepository)

    @Provides
    @Singleton
    fun provideAuthViewModel(userRepository: UserRepository): PhoneAuthViewModel = PhoneAuthViewModel(userRepository)

    @Provides
    fun provideTaskUpdateViewModel(taskRepository: TaskRepository): TaskUpdateViewModel = TaskUpdateViewModel(taskRepository)

    @Provides
    @Singleton
    fun provideTaskViewModel(taskRepository: TaskRepository, userRepository: UserRepository): TaskViewModel = TaskViewModel(taskRepository, userRepository)

    @Provides
    @Singleton
    fun provideTaskDetailViewModel(taskRepository: TaskRepository): TaskDetailViewModel = TaskDetailViewModel(taskRepository)
}