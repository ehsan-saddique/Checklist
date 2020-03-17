package com.prismosis.checklist.ui.task

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.prismosis.checklist.R
import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.ui.launcher.LauncherActivity
import com.prismosis.checklist.utils.Utils

import kotlinx.android.synthetic.main.activity_task_list.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.ui.taskdetail.TaskDetailActivity
import com.prismosis.checklist.utils.ChecklistApplication
import com.prismosis.checklist.utils.Enum
import javax.inject.Inject


class TaskListActivity : AppCompatActivity(), ClickListener {

    @Inject lateinit var taskViewModel: TaskViewModel
    private lateinit var mAdapter: TaskListAdapter
    private lateinit var progressDialog: ProgressDialog
    private var syncMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Checklist"
        ChecklistApplication.instance?.appComponent?.inject(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val emptyView = findViewById<RelativeLayout>(R.id.empty_view)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Syncing data")
        progressDialog.setMessage("Please wait..")
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = true
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        mAdapter = TaskListAdapter(ArrayList<DTOTask>(), this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TaskListActivity)
            adapter = mAdapter
        }


        taskViewModel.getAllTasks().observe(this, Observer { tasks ->
            mAdapter.setTaks(tasks)

            if (tasks.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        })

        taskViewModel.getDirtyTasksCount().observe(this, Observer { count ->
           if (count > 0) {
               syncMenuItem?.setIcon(R.drawable.ic_sync_warning)
           }
            else {
               syncMenuItem?.setIcon(R.drawable.ic_sync_data)
           }
        })

        taskViewModel.taskResult.observe(this, Observer { taskResult ->

            if (taskResult.error != null) {
                progressDialog.dismiss()
                showError(taskResult.error!!)
            }

            if (taskResult.success != null) {
                progressDialog.dismiss()
                showSuccess(taskResult.success!!)
            }
        })

        if (!Utils.instance.isTasksFetched()) {
            progressDialog.setTitle("Fetching data")
            progressDialog.show()
            taskViewModel.fetchDataFromCloud()
        }

        if (savedInstanceState?.getBoolean("isDialogShowing", false) ?: false) {
            savedInstanceState?.putBoolean("isDialogShowing", false)
            progressDialog.show()
        }


        fab.setOnClickListener { view ->
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showError(errorMsg: String) {
        Utils.instance.showSnackBar(window.decorView.rootView, errorMsg)
    }

    private fun showSuccess(successMsg: String) {
        Utils.instance.showSnackBar(window.decorView.rootView, successMsg, isSticky = false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)
        syncMenuItem = menu?.findItem(R.id.action_sync_data)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {
            Utils.instance.showDialog(this,null, "Are you sure you want to logout? Tasks which have not been synced with server will be deleted.", "Logout", true, DialogInterface.OnClickListener { _, _ ->
                taskViewModel.logoutUser()
                finish()
                val intent = Intent(this@TaskListActivity, LauncherActivity::class.java)
                startActivity(intent)
            })

            true
        }
        R.id.action_sync_data -> {

            progressDialog.show()
            taskViewModel.syncDataWithCloud()

            true
        }
        R.id.action_force_push -> {

            Utils.instance.showDialog(this,null, "This will upload all your changed tasks, but it will download all the data after that. So it might take some time to sync.", "Sync Everything", true, DialogInterface.OnClickListener { _, _ ->
                progressDialog.show()
                taskViewModel.forceSyncData()
            })

            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(task: DTOTask) {
        val intent = Intent(this, TaskDetailActivity::class.java)
        intent.putExtra("taskId", task.taskId)
        startActivity(intent)
    }

    override fun onEditClick(task: DTOTask) {
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("task", task)
        startActivity(intent)
    }

    override fun onDeleteClick(task: DTOTask) {
        Utils.instance.showDialog(this, null, "Are you sure you want to delete this task? All of the sub tasks will also be deleted.",
            "Delete", true, DialogInterface.OnClickListener { _, _ ->
                taskViewModel.deleteTask(task)
            })
    }

    override fun onChangeStatusClick(task: DTOTask) {
        showChangeStatusDialog(task)
    }

    private fun showChangeStatusDialog(task: DTOTask) {

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Change Status to")
        val dataSource = listOf<String>(Enum.TaskStatus.PENDING.string,
            Enum.TaskStatus.INPROGRESS.string,
            Enum.TaskStatus.COMPLETED.string)

        val currentStatus = task.status
        val checkedItem = dataSource.indexOf(currentStatus.string)

        dialogBuilder.setSingleChoiceItems(dataSource.toTypedArray(), checkedItem, DialogInterface.OnClickListener { dialog, item ->

            val selectedItemStr = dataSource[item]
            val selectedStatus = Enum.TaskStatus.getValueFromString(selectedItemStr)

            taskViewModel.changeTaskStatus(task, selectedStatus)

            dialog.dismiss()
        })

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }

    override fun onBackPressed() {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isDialogShowing", progressDialog.isShowing)
        taskViewModel.taskResult.value?.error = null
        taskViewModel.taskResult.value?.success = null
    }

}
