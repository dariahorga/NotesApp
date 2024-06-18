package com.example.notesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityShowNotesBinding

class NotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowNotesBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)
        notesAdapter = NotesAdapter(emptyList(), emptyMap(), this)

        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@NotesActivity)
            adapter = notesAdapter
        }

        val folderId = intent.getIntExtra("folder_id", -1)
        if (folderId != -1) {
            displayNotes(folderId)
        } else {
            finish() // Finish activity if folder_id is not provided
        }
    }

    private fun displayNotes(folderId: Int) {
        val notes = db.getNotesByFolderId(folderId)
        notesAdapter.refreshData(notes)
    }
}
