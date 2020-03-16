package com.prismosis.checklist.dependencies

import com.prismosis.checklist.ui.authentication.login.LoginActivity
import com.prismosis.checklist.ui.authentication.phone.PhoneAuthenticationActivity
import com.prismosis.checklist.ui.authentication.signup.SignupActivity
import com.prismosis.checklist.ui.task.AddEditActivity
import com.prismosis.checklist.ui.task.TaskListActivity
import com.prismosis.checklist.ui.taskdetail.TaskDetailActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Ehsan Saddique on 2020-03-16
 */

@Singleton
@Component(modules = [RepositoryModule::class, ViewModelModule::class, ServiceModule::class])
interface AppComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: SignupActivity)
    fun inject(activity: PhoneAuthenticationActivity)
    fun inject(activity: TaskListActivity)
    fun inject(activity: TaskDetailActivity)
    fun inject(activity: AddEditActivity)

}