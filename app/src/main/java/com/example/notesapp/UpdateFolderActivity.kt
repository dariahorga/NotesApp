package com.example.notesapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityUpdateFolderBinding
import com.example.notesapp.NotesDatabaseHelper

class UpdateFolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateFolderBinding
    private lateinit var db: NotesDatabaseHelper
    private var folderId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        // Extrage datele primite din intent
        folderId = intent.getIntExtra("folderId", -1)
        val folderName = intent.getStringExtra("folderName")

        // Populează câmpul de editare cu numele folderului curent
        binding.folderNameEditText.setText(folderName)

        binding.saveFolderButton.setOnClickListener {
            val newFolderName = binding.folderNameEditText.text.toString().trim()

            if (newFolderName.isNotBlank()) {
                // Actualizează numele folderului în baza de date
                db.updateFolderName(folderId, newFolderName)

                // Actualizează numele folderului în toate notele asociate acestuia
                db.updateNotesFolderName(folderId, newFolderName)

                Toast.makeText(this, "Folder updated", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Folder name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
