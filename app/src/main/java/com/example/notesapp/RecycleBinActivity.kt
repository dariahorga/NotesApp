package com.example.notesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityRecycleBinBinding

class RecycleBinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecycleBinBinding
    private lateinit var viewModel: RecycleBinViewModel
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel using ViewModelProvider
        viewModel = ViewModelProvider(this).get(RecycleBinViewModel::class.java)

        // Setup RecyclerView and NotesAdapter with restoreNoteCallback
        notesAdapter = NotesAdapter(emptyList(), emptyMap(), this) { noteId ->
            viewModel.restoreNoteFromRecycleBin(noteId)
        }
        binding.recycleBinRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recycleBinRecyclerView.adapter = notesAdapter

        // Observe LiveData from ViewModel
        viewModel.deletedNotes.observe(this) { notes ->
            notesAdapter.refreshData(notes)
        }
    }
}







