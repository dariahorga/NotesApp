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

        // initializam baza de date
        db = NotesDatabaseHelper(this)

        // setam click listener pe butonul de adaugare folder
        binding.addFolderButton.setOnClickListener {
            val folderName = binding.folderNameEditText.text.toString().trim()

            if (folderName.isNotEmpty()) {
                // cream un obiect folder nou
                val folder = Folder(0, folderName)
                // inseram folderul in baza de date
                db.insertFolder(folder)
                Toast.makeText(this, "Folder added", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // afisam mesaj daca numele folderului e gol
                Toast.makeText(this, "Please enter a folder name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
