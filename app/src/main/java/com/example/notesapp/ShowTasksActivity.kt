package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notesapp.databinding.ActivityShowTasksBinding

class ShowTasksActivity : AppCompatActivity() {
    private lateinit var binding : ActivityShowTasksBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var tasksAdapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initializam baza de date
        db = NotesDatabaseHelper(this)

        // preluam toate taskurile din baza de date
        val tasks = db.getAllTasks()
        tasksAdapter = TasksAdapter(tasks, this)
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = tasksAdapter

        // setam click listener pe butonul de adaugare task
        binding.addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // setam click listener pe butonul de intoarcere
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setupEdgeToEdge()
    }

    override fun onResume() {
        super.onResume()
        // actualizam lista de taskuri la revenirea in activitate
        tasksAdapter.refreshData(db.getAllTasks())
    }

    private fun setupEdgeToEdge() {
        // configuram vizualizarea pentru a se intinde pana la marginile ecranului
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }
}
