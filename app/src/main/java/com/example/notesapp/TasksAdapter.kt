package com.example.notesapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter (var tasks: List<Task>, private val context: Context) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val dataExpirareTextView: TextView = itemView.findViewById(R.id.dataExpirareTextView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksAdapter.TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TasksAdapter.TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TasksAdapter.TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.contentTextView.text = tasks[position].content
        holder.dataExpirareTextView.text = tasks[position].dataExpirare
        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
            }
            holder.itemView.context.startActivity(intent)
        }
        holder.deleteButton.setOnClickListener {
            val db = NotesDatabaseHelper(holder.itemView.context)
            db.deleteTask(task.id)
            refreshData(db.getAllTasks())
            Toast.makeText(holder.itemView.context, "Task Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newTasks: List<Task>){
        tasks = newTasks
        notifyDataSetChanged()
    }
}