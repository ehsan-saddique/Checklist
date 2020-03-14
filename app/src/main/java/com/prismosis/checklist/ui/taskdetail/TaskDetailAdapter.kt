package com.prismosis.checklist.ui.taskdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.prismosis.checklist.R
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.ui.task.ClickListener
import com.prismosis.checklist.ui.task.TaskItemViewHolder
import com.prismosis.checklist.utils.Utils

/**
 * Created by Ehsan Saddique on 2020-03-14
 */

class TaskDetailAdapter(private var tasks: List<DTOTask>, private var listener: ClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 1) {
            val itemView = inflater.inflate(R.layout.task_detail_item, parent, false)
            return TaskItemViewHolder(itemView, true)
        }
        else {
            val itemView = inflater.inflate(R.layout.task_item, parent, false)
            return TaskItemViewHolder(itemView)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = tasks.get(position)
        val taskHolder = holder as TaskItemViewHolder
        taskHolder.bind(task, listener)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            1
        } else {
            2
        }
    }

    fun setTaks(tasks: List<DTOTask>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }


//    class TaskItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private var taskName: TextView
//        private var taskDescription: TextView
//        private var startDate: TextView
//        private var endDate: TextView
//        private var subTasks: TextView
//        private var taskStatus: TextView
//        private var toolbar: Toolbar
//
//        init {
//            taskName = itemView.findViewById(R.id.task_name)
//            taskDescription = itemView.findViewById(R.id.task_description)
//            startDate = itemView.findViewById(R.id.task_start_date)
//            endDate = itemView.findViewById(R.id.task_end_date)
//            subTasks = itemView.findViewById(R.id.sub_tasks)
//            taskStatus = itemView.findViewById(R.id.task_status)
//            toolbar = itemView.findViewById(R.id.toolbar_task_item)
//            toolbar.inflateMenu(R.menu.menu_task_item)
//        }
//
//        fun bind(task: DTOTask, listener: ClickListener) {
//            taskName.setText(task.name)
//            taskDescription.setText(if (task.description.isNullOrEmpty()) "No description" else task.description)
//            startDate.setText("Starts: " + Utils.stringFromDate(task.startDate))
//            endDate.setText("Ends: " + Utils.stringFromDate(task.endDate))
//            subTasks.setText("Sub Tasks: 0")
//            taskStatus.setText(task.status.string)
//            taskStatus.setBackgroundResource(task.status.drawableId)
//
//            toolbar.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.action_edit -> {
//                        listener.onEditClick(task)
//                        true
//                    }
//                    R.id.action_change_status -> {
//                        listener.onChangeStatusClick(task)
//                        true
//                    }
//                    R.id.action_delete -> {
//                        listener.onDeleteClick(task)
//                        true
//                    }
//                    else -> {
//                        false
//                    }
//                }
//            }
//        }
//    }

}