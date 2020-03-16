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
    private val VIEW_TYPE_TASK = 1
    private val VIEW_TYPE_SUB_TASK = 2
    private val VIEW_TYPE_EMPTY_SUB_TASK = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEW_TYPE_TASK) {
            val itemView = inflater.inflate(R.layout.task_detail_item, parent, false)
            return TaskItemViewHolder(itemView, true)
        }
        else if (viewType == VIEW_TYPE_EMPTY_SUB_TASK) {
            val itemView = inflater.inflate(R.layout.task_detail_item_empty, parent, false)
            return SubTaskEmptyViewHolder(itemView)
        }
        else {
            val itemView = inflater.inflate(R.layout.task_item, parent, false)
            return TaskItemViewHolder(itemView)
        }
    }

    override fun getItemCount(): Int {
        if (tasks.size == 1) {
            return tasks.size + 1
        }
        else {
            return tasks.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 1 && tasks.size == 1) {
            //Bind empty sub tasks view
        }
        else {
            val task = tasks.get(position)
            val taskHolder = holder as TaskItemViewHolder
            taskHolder.bind(task, listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_TASK
        }
        else if (position == 1 && tasks.size == 1) {
            VIEW_TYPE_EMPTY_SUB_TASK
        }
        else {
            VIEW_TYPE_SUB_TASK
        }
    }

    fun setTaks(tasks: List<DTOTask>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }


    class SubTaskEmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {}
    }

}