package com.example.notesapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var folderId: Int = -1
    private var folderName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        folderId = intent.getIntExtra("folderId", -1)
        folderName = intent.getStringExtra("folderName")

        supportActionBar?.title = "Add Note"

        setupFolderSpinner()

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()

            if (title.isNotBlank()) {
                val note = Note(0, title, content, folderId)
                db.insertNote(note)
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFolderSpinner() {
        val folders = db.getAllFolders()
        val folderNames = folders.map { it.name }

        val folderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, folderNames)
        folderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.folderSpinner.adapter = folderAdapter

        // Selectează folderul curent în spinner (dacă există)
        folderName?.let {
            val position = folderNames.indexOf(it)
            if (position != -1) {
                binding.folderSpinner.setSelection(position)
            }
        }
    }
}

