package com.example.notesapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityUpdateTaskBinding
import android.widget.CalendarView.OnDateChangeListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class UpdateTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateTaskBinding
    private lateinit var db: NotesDatabaseHelper
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        taskId = intent.getIntExtra("task_id", -1)
        if (taskId == -1) {
            finish()
            return
        }

        val task = db.getTaskById(taskId)

        var date : String = ""

        binding.calendarView.setOnDateChangeListener(
            OnDateChangeListener { view, year, month, dayOfMonth ->
                date = (dayOfMonth.toString()+"-"+(month+1)+"-"+year)
            }
        )

        binding.contentEditText.setText(task?.content ?: "")

        val zi : Int
        val luna : Int
        val an : Int


        if(task?.dataExpirare!=null)
        {
            val dataParsata = task.dataExpirare.split("-")
            zi = dataParsata[0].toInt()
            luna = dataParsata[1].toInt()
            an = dataParsata[2].toInt()

            val calendar = Calendar.getInstance()
            calendar.set(an, luna - 1, zi)

            val timeInMillis = calendar.timeInMillis
            binding.calendarView.setDate(timeInMillis)
        }


        binding.saveButton.setOnClickListener {
            val newContent = binding.contentEditText.text.toString()
            var updatedTask : Task
            if (task != null) {
                if(date.isBlank())
                {
                    updatedTask = Task(task.id, newContent, task.dataExpirare, task.checked)
                }
                else
                {
                    updatedTask = Task(task.id, newContent, date, task.checked)
                }
                db.updateTask(updatedTask)
                finish()
                Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}