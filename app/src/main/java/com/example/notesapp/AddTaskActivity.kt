package com.example.notesapp

import android.content.Context
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.notesapp.databinding.ActivityAddTaskBinding
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var db: NotesDatabaseHelper

    lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initializam baza de date
        db = NotesDatabaseHelper(this)

        var date: String = ""

        // setam listener pe calendar view pentru a prelua data selectata
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            date = (dayOfMonth.toString() + "-" + (month + 1) + "-" + year)
        }

        // setam click listener pe butonul de salvare
        binding.saveButton.setOnClickListener {
            val content = binding.contentEditText.text.toString()
            if (content.isEmpty()) {
                // afisam mesaj daca continutul este gol
                Toast.makeText(this, "Content cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                if (date.isEmpty()) {
                    // afisam mesaj daca data nu este selectata
                    Toast.makeText(this, "Select a deadline", Toast.LENGTH_SHORT).show()
                } else {
                    // cream un obiect task nou
                    val task = Task(0, content, date, 0, null)  // Initialize eventId as null
                    db.insertTask(task)
                    if (MainActivity.isCalendarServiceInitialized) {
                        val taskJson = Gson().toJson(task)
                        val workRequest = OneTimeWorkRequestBuilder<TaskWorker>()
                            .setInputData(workDataOf("task" to taskJson))
                            .build()
                        WorkManager.getInstance(this).enqueue(workRequest)
                        Toast.makeText(this, "Task added to Google Calendar", Toast.LENGTH_SHORT).show()
                    } else {
                        // afisam mesaj daca serviciul de calendar nu este initializat
                        Toast.makeText(this, "Calendar service not initialized", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
        }
    }

    class TaskWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val taskJson = inputData.getString("task") ?: return Result.failure()
            val task = Gson().fromJson(taskJson, Task::class.java)
            return try {
                val calendarService = MainActivity.calendarService

                val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                val date = inputFormat.parse(task.dataExpirare)
                val dateTimeString = outputFormat.format(date)

                // cream un eveniment pentru calendar
                val event = Event()
                    .setSummary(task.content)
                    .setDescription("Task from NotesApp")

                val dateTimeStart = DateTime(dateTimeString)
                val start = EventDateTime().setDateTime(dateTimeStart).setTimeZone("America/Los_Angeles")
                event.start = start

                val dateTimeEnd = DateTime(dateTimeString)
                val end = EventDateTime().setDateTime(dateTimeEnd).setTimeZone("America/Los_Angeles")
                event.end = end

                val calendarId = "primary"
                val createdEvent = calendarService.events().insert(calendarId, event).execute()

                // actualizam task-ul cu eventId si salvam in baza de date
                val db = NotesDatabaseHelper(context)
                task.eventId = createdEvent.id
                db.updateTask(task)

                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}
