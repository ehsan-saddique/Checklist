## Introduction
This is basically a To-Do app. You can add tasks, and subtasks against any tasks. Here's how it works.
* You can signup using email or phone number.
* All your data is then synced with cloud in real time.
* You can add a task providing start date and end date of that task.
* Task is added in `Pending` status by default.
* You can change task status to `In-Progress` or `Completed`.
* After the end date of task is reached, task is moved to `Expired` status automatically.
* A task can have multiple subtasks. And each subtask can again have multiple subtasks, and so on.
* If a task has subtasks, when all the subtasks status is `Completed`, that task's status is also changed to `Completed` automatically.
* Every task is synced with cloud as soon as it is created.
* If task couldn't be synced due to network connectivity, then it will be synced as soon as the internet is available.
* Data syncing is in real time, if you logged in on another device, you will be able to see newly created tasks or task changes right away.
* If you delete the app and reinstall, all of your data will be still available after it's fetched from the cloud.
## Technologies Used
* MVVM
* Unit Test
* Firebase
* Kotlin
* Android Architecture Components
* LiveData
* ViewModels
* Room
* Dependency Injection
* Dagger
* Retrofit
* Okhttp
* Material Design

## Preview

![alt-text](app/src/main/java/com/prismosis/checklist/utils/ChecklistGIF.gif)
