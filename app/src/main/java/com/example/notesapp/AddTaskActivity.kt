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

        var date : String = ""

        binding.calendarView.setOnDateChangeListener(
            OnDateChangeListener { view, year, month, dayOfMonth ->
                date = (dayOfMonth.toString()+"-"+(month+1)+"-"+year)
            }
        )

        binding.saveButton.setOnClickListener {
            val content = binding.contentEditText.text.toString()
            if (content.isEmpty()) {
                Toast.makeText(this, "Content cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if(date.isEmpty())
                {
                    Toast.makeText(this, "Select a deadline", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val task = Task(0, content, date, 0)
                    db.insertTask(task)
                    finish()
                }
            }
        }
    }
}