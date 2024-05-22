package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var folderAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        setupFolderSpinner()

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val selectedFolderName = binding.folderSpinner.selectedItem.toString()
            val selectedFolderId = db.getFolderIdByName(selectedFolderName)
            if (title.isNotBlank()) {
                val note = Note(0, title, content, selectedFolderId)
                db.insertNote(note)
                finish()
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFolderSpinner() {
        val folders = db.getAllFolders()
        val folderNames = folders.map { it.name }

        folderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, folderNames)
        folderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.folderSpinner.adapter = folderAdapter
    }
}
