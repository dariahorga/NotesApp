package com.example.notesapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityUpdateNoteBinding

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        val note = db.getNoteById(noteId)


        binding.updateTitleEditText.setText(note?.title ?: "")
        binding.updateContentEditText.setText(note?.content ?: "")

        setupFolderSpinner()

        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()
            val selectedFolderName = binding.folderSpinner.selectedItem.toString()
            val selectedFolderId = db.getFolderIdByName(selectedFolderName)


            if (note != null) {
                val updatedNote = Note(note.id, newTitle, newContent, selectedFolderId)
                db.updateNote(updatedNote)

                finish()
                Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
            } else {
                // Handle the case when note is null
                Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFolderSpinner() {
        val folders = db.getAllFolders()
        val folderNames = folders.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, folderNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.folderSpinner.adapter = adapter

        val note = db.getNoteById(noteId)
        val noteFolderId = note?.folderId ?: -1
        val folderPosition = folders.indexOfFirst { it.id == noteFolderId }
        if (folderPosition != -1) {
            binding.folderSpinner.setSelection(folderPosition)
        }
    }
}
