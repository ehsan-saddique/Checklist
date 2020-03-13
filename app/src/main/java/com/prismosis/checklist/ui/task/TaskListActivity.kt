package com.prismosis.checklist.ui.task

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
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

class TaskListActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var mAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Checklist"

        taskViewModel = ViewModelProviders.of(this, TaskViewModelFactory())
            .get(TaskViewModel::class.java)

        mAdapter = TaskListAdapter(ArrayList<Task>())
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TaskListActivity)
            adapter = mAdapter
        }


        taskViewModel.getAllTasks().observe(this, Observer { tasks ->
            mAdapter.setTaks(tasks)
        })


        fab.setOnClickListener { view ->
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {

            Utils.showDialog(this,null, "Are you sure you want to logout?", "Logout", true, DialogInterface.OnClickListener { _, _ ->
                FirebaseAuth.getInstance().signOut()
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

}
