package com.prismosis.checklist.ui.taskdetail

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
import androidx.appcompat.app.AlertDialog
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.ui.task.AddEditActivity
import com.prismosis.checklist.ui.task.ClickListener
import com.prismosis.checklist.utils.ChecklistApplication
import com.prismosis.checklist.utils.Enum
import javax.inject.Inject


class TaskDetailActivity : AppCompatActivity(), ClickListener {

    @Inject
    lateinit var taskDetailViewModel: TaskDetailViewModel
    private lateinit var mAdapter: TaskDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Task Detail"
        ChecklistApplication.instance?.appComponent?.inject(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val emptyView = findViewById<RelativeLayout>(R.id.empty_view)

        val taskId = intent.extras?.getString("taskId") ?: ""

        mAdapter = TaskDetailAdapter(ArrayList<DTOTask>(), this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TaskDetailActivity)
            adapter = mAdapter
        }


        taskDetailViewModel.getAllSubTasks(taskId).observe(this, Observer { tasks ->
            mAdapter.setTaks(tasks)
            if (!tasks.isEmpty()) {
                val task = tasks.get(0)
                if (task.isTaskExpired()) {
                    fab.visibility = View.GONE
                }
            }
        })

        taskDetailViewModel.taskResult.observe(this, Observer {
            val taskResult = it ?: return@Observer

            if (taskResult.error != null) {
                showError(taskResult.error!!)
            }

            if (taskResult.success != null) {
                showSuccess(taskResult.success!!)
            }
        })


        fab.setOnClickListener { view ->
            val intent = Intent(this, AddEditActivity::class.java)
            intent.putExtra("parentId", taskId)
            startActivity(intent)
        }
    }

    private fun showError(errorMsg: String) {
        Utils.instance.showSnackBar(window.decorView.rootView, errorMsg)
    }

    private fun showSuccess(successMsg: String) {
        Utils.instance.showSnackBar(window.decorView.rootView, successMsg, isSticky = false)
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
                taskDetailViewModel.deleteTask(task)
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

            taskDetailViewModel.changeTaskStatus(task, selectedStatus)

            dialog.dismiss()
        })

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }

}
