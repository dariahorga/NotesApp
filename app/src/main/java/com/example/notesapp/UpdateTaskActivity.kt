package com.example.notesapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.notesapp.databinding.ActivityUpdateTaskBinding
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateTaskBinding
    private lateinit var db: NotesDatabaseHelper
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initializam baza de date
        db = NotesDatabaseHelper(this)

        // preluam taskId din intent
        taskId = intent.getIntExtra("task_id", -1)
        if (taskId == -1) {
            finish()
            return
        }

        val task = db.getTaskById(taskId)

        var date: String = ""

        // setam listener pe calendar view pentru a prelua data selectata
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            date = (dayOfMonth.toString() + "-" + (month + 1) + "-" + year)
        }

        // setam continutul task-ului in campul de text
        binding.contentEditText.setText(task?.content ?: "")

        val zi: Int
        val luna: Int
        val an: Int

        if (task?.dataExpirare != null) {
            val dataParsata = task.dataExpirare.split("-")
            zi = dataParsata[0].toInt()
            luna = dataParsata[1].toInt()
            an = dataParsata[2].toInt()

            val calendar = Calendar.getInstance()
            calendar.set(an, luna - 1, zi)

            val timeInMillis = calendar.timeInMillis
            binding.calendarView.setDate(timeInMillis)
        }

        // setam click listener pe butonul de salvare
        binding.saveButton.setOnClickListener {
            val newContent = binding.contentEditText.text.toString()
            if (newContent.isEmpty()) {
                Toast.makeText(this, "Content cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                val updatedTask: Task
                if (task != null) {
                    updatedTask = if (date.isBlank()) {
                        Task(task.id, newContent, task.dataExpirare, task.checked, task.eventId)
                    } else {
                        Task(task.id, newContent, date, task.checked, task.eventId)
                    }
                    db.updateTask(updatedTask)
                    if (MainActivity.isCalendarServiceInitialized) {
                        val taskJson = Gson().toJson(updatedTask)
                        val workRequest = OneTimeWorkRequestBuilder<UpdateTaskWorker>()
                            .setInputData(workDataOf("task" to taskJson))
                            .build()
                        WorkManager.getInstance(this).enqueue(workRequest)
                        Toast.makeText(this, "Task updated in Google Calendar", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Calendar service not initialized", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                } else {
                    Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    class UpdateTaskWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val taskJson = inputData.getString("task") ?: return Result.failure()
            val task = Gson().fromJson(taskJson, Task::class.java)
            return try {
                val calendarService = MainActivity.calendarService

                val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                val date = inputFormat.parse(task.dataExpirare)
                val dateTimeString = outputFormat.format(date)

                // preluam evenimentul din calendar
                val event = calendarService.events().get("primary", task.eventId).execute()

                event.summary = task.content
                event.description = "Task from NotesApp"

                val dateTimeStart = DateTime(dateTimeString)
                val start = EventDateTime().setDateTime(dateTimeStart).setTimeZone("America/Los_Angeles")
                event.start = start

                val dateTimeEnd = DateTime(dateTimeString)
                val end = EventDateTime().setDateTime(dateTimeEnd).setTimeZone("America/Los_Angeles")
                event.end = end

                // actualizam evenimentul in calendar
                calendarService.events().update("primary", event.id, event).execute()

                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}
