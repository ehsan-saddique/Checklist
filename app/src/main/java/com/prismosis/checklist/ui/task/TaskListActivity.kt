package com.prismosis.checklist.ui.task

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
import com.prismosis.checklist.utils.Enum


class TaskListActivity : AppCompatActivity(), ClickListener {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var mAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Checklist"

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val emptyView = findViewById<RelativeLayout>(R.id.empty_view)

        taskViewModel = ViewModelProviders.of(this, TaskViewModelFactory())
            .get(TaskViewModel::class.java)

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

        taskViewModel.taskResult.observe(this, Observer {
            val taskResult = it ?: return@Observer

            if (taskResult.error != null) {
                showError(taskResult.error)
            }

            if (taskResult.success != null) {
                showSuccess(taskResult.success)
            }
        })


        fab.setOnClickListener { view ->
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showError(errorMsg: String) {
        Utils.showSnackBar(window.decorView.rootView, errorMsg)
    }

    private fun showSuccess(successMsg: String) {
        Utils.showSnackBar(window.decorView.rootView, successMsg, isSticky = false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {

            Utils.showDialog(this,null, "Are you sure you want to logout? Tasks which have not been synced with server will be deleted.", "Logout", true, DialogInterface.OnClickListener { _, _ ->
                FirebaseAuth.getInstance().signOut()
                taskViewModel.deleteAllTasks()
                finish()
                val intent = Intent(this@TaskListActivity, LauncherActivity::class.java)
                startActivity(intent)
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
        intent.putExtra("taskId", task.id)
        startActivity(intent)
    }

    override fun onEditClick(task: DTOTask) {
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("task", task)
        startActivity(intent)
    }

    override fun onDeleteClick(task: DTOTask) {
        Utils.showDialog(this, null, "Are you sure you want to delete this task? All of the sub tasks will also be deleted.",
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
            Enum.TaskStatus.COMPLETED.string,
            Enum.TaskStatus.EXPIRED.string)

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

}
