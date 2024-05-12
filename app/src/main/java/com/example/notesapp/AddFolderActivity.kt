package com.example.notesapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityAddFolderBinding

class AddFolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFolderBinding
    private lateinit var db: NotesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        binding.addFolderButton.setOnClickListener {
            val folderName = binding.folderNameEditText.text.toString().trim()

            if (folderName.isNotEmpty()) {
                val folder = Folder(0, folderName)
                db.insertFolder(folder)
                Toast.makeText(this, "Folder added", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter a folder name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
