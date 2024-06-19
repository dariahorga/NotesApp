package com.example.notesapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter(var tasks: List<Task>, private val context: Context) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    private var db: NotesDatabaseHelper = NotesDatabaseHelper(context)

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val dataExpirareTextView: TextView = itemView.findViewById(R.id.dataExpirareTextView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val checkBoxTask: CheckBox = itemView.findViewById(R.id.checkBoxTask)
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
        holder.checkBoxTask.isChecked = task.checked == 1

        // resetam listenerul pentru a preveni declansarea sa in mod accidental
        holder.checkBoxTask.setOnCheckedChangeListener(null)

        holder.checkBoxTask.isChecked = task.checked == 1

        // setam listener pentru checkBox pentru a actualiza starea taskului
        holder.checkBoxTask.setOnCheckedChangeListener { _, isChecked ->
            val updatedTask: Task
            val value = if (isChecked) 1 else 0
            updatedTask = Task(task.id, task.content, task.dataExpirare, value)
            db.updateTask(updatedTask)
            refreshData(db.getAllTasks())
        }

        // setam click listener pentru butonul de actualizare task
        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        // setam click listener pentru butonul de stergere task
        holder.deleteButton.setOnClickListener {
            val db = NotesDatabaseHelper(holder.itemView.context)
            db.deleteTask(task.id)
            refreshData(db.getAllTasks())
            Toast.makeText(holder.itemView.context, "Task Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    // metoda pentru actualizarea datelor in adapter
    fun refreshData(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
