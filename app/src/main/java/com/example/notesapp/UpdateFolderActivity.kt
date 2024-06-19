package com.example.notesapp

import android.app.AlertDialog
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

        // initializam baza de date
        db = NotesDatabaseHelper(this)

        // preluam folderId si folderName din intent
        folderId = intent.getIntExtra("folderId", -1)
        val folderName = intent.getStringExtra("folderName")

        // setam numele folderului in EditText
        binding.folderNameEditText.setText(folderName)

        // setam click listener pe butonul de salvare
        binding.saveFolderButton.setOnClickListener {
            val newFolderName = binding.folderNameEditText.text.toString().trim()

            if (newFolderName.isNotBlank()) {
                // actualizam numele folderului in baza de date
                db.updateFolderName(folderId, newFolderName)

                // actualizam numele folderului in notele asociate
                db.updateNotesFolderName(folderId, newFolderName)

                Toast.makeText(this, "Folder updated", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Folder name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
