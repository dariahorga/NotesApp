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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityShowTasksBinding
import com.google.android.material.navigation.NavigationView

class ShowTasksActivity : AppCompatActivity() {
    private lateinit var binding : ActivityShowTasksBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var tasksAdapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityShowTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        val tasks = db.getAllTasks()
        tasksAdapter = TasksAdapter(tasks, this)
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = tasksAdapter

        binding.addTaskButton.setOnClickListener{
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        setupEdgeToEdge()
    }

    override fun onResume() {
        super.onResume()
        tasksAdapter.refreshData(db.getAllTasks())
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }
}