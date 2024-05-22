package com.example.notesapp

import android.content.Intent
import java.util.Calendar
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityAddTaskBinding
import android.widget.CalendarView.OnDateChangeListener

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var db: NotesDatabaseHelper

    lateinit var calendarView : CalendarView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        calendarView = findViewById(R.id.calendarView)

        var date : String = ""

        calendarView.setOnDateChangeListener(
            OnDateChangeListener { view, year, month, dayOfMonth ->
                date = (dayOfMonth.toString()+"-"+(month+1)+"-"+year)
            }
        )

        binding.saveButton.setOnClickListener{

            val content = binding.contentEditText.text.toString()
            val task = Task(0, content, date, 0)
            db.insertTask(task)
            finish()
        }
    }
}