package com.prismosis.checklist.ui.task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.prismosis.checklist.R
import com.prismosis.checklist.utils.Utils
import android.widget.TimePicker
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import com.prismosis.checklist.data.model.Task
import java.util.*


class AddEditActivity : AppCompatActivity() {

    private lateinit var taskUpdateViewModel: TaskUpdateViewModel
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private var isEditScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Add Task"

        val name = findViewById<EditText>(R.id.task_name)
        val description = findViewById<EditText>(R.id.task_description)
        startDate = findViewById<EditText>(R.id.task_start_date)
        endDate = findViewById<EditText>(R.id.task_end_date)
        val btnUpdate = findViewById<Button>(R.id.add_button)


        var task: Task? = null
        if (intent.hasExtra("task")) {
            supportActionBar?.title = "Edit Task"
            btnUpdate.setText("Update")
            isEditScreen = true

            task = intent.extras?.get("task") as? Task
            task?.let {
                name.setText(task.name)
                description.setText(task.description)
                startDate.setText(Utils.stringFromDate(task.startDate))
                endDate.setText(Utils.stringFromDate(task.endDate))
            }
        }


        taskUpdateViewModel = ViewModelProviders.of(this, TaskUpdateViewModelFactory())
            .get(TaskUpdateViewModel::class.java)


        taskUpdateViewModel.taskUpdateResult.observe(this, Observer {
            val taskResult = it ?: return@Observer

            if (taskResult.error != null) {
                showError(taskResult.error)
            }

            if (taskResult.success != null) {
                onSuccess(taskResult.success)
            }
        })

        startDate.setOnClickListener {
            showDateTimePicker(startDate)
        }

        endDate.setOnClickListener {
            showDateTimePicker(endDate)
        }

        btnUpdate.setOnClickListener(View.OnClickListener {
            Utils.hideSoftKeyboard(this)

            val nameStr = name.text.toString()
            val descriptionStr = description.text.toString()
            val startDateStr = startDate.text.toString()
            val endDateStr = endDate.text.toString()

            if (taskUpdateViewModel.isFormValid(nameStr, startDateStr, endDateStr)) {
                if (isEditScreen) {
                    taskUpdateViewModel.updateTask(task?.id ?: "", nameStr, descriptionStr, startDateStr, endDateStr)
                }
                else {
                    taskUpdateViewModel.addTask(null, nameStr, descriptionStr, startDateStr, endDateStr)
                }
            }
        })
    }

    private fun onSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun showError(errorMessage: String) {
        Utils.showSnackBar(window.decorView.rootView, errorMessage)
    }

    private fun showDateTimePicker(editText: EditText) {
        val dialogView = View.inflate(this, R.layout.date_time_picker, null)
        val alertDialog = AlertDialog.Builder(this).create()

        val datePicker = dialogView.findViewById(R.id.date_picker) as DatePicker
        val timePicker = dialogView.findViewById(R.id.time_picker) as TimePicker
        datePicker.minDate = Date().time

        if (!editText.text.toString().isEmpty()) {
            val alreadySelectedDate = Utils.dateFromString(editText.text.toString())
            val calendar = Calendar.getInstance()
            calendar.time = alreadySelectedDate

            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = calendar.get(Calendar.MINUTE)
        }

        if (editText === endDate) {
            if (!startDate.text.toString().isEmpty()) {
                datePicker.minDate = Utils.dateFromString(startDate.text.toString()).time
            }
        }

        dialogView.findViewById<Button>(R.id.date_time_set).setOnClickListener(View.OnClickListener {

            val calendar = GregorianCalendar(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                timePicker.hour,
                timePicker.minute
            )

            val dateTime = Date(calendar.getTimeInMillis())
            editText.setText(Utils.stringFromDate(dateTime))
            alertDialog.dismiss()
        })
        alertDialog.setView(dialogView)
        alertDialog.show()
    }
}
