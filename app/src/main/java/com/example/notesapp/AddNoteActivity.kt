package com.example.notesapp

import android.app.Activity
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

        // initializam baza de date
        db = NotesDatabaseHelper(this)

        // preluam folderId si folderName din intent
        folderId = intent.getIntExtra("folderId", -1)
        folderName = intent.getStringExtra("folderName")

        // setam titlul action bar-ului
        supportActionBar?.title = "Add Note"

        // configuram spinner-ul pentru foldere
        setupFolderSpinner()

        // setam click listener pe butonul de salvare
        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val selectedFolderName = binding.folderSpinner.selectedItem as String
            val selectedFolderId = db.getFolderIdByName(selectedFolderName)

            if (title.isNotBlank()) {
                // cream o notita noua
                val note = Note(0, title, content, selectedFolderId)
                // inseram notita in baza de date
                db.insertNote(note)
                setResult(Activity.RESULT_OK)
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // afisam mesaj daca titlul este gol
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

        // selectam folderul curent in spinner (daca exista)
        folderName?.let {
            val position = folderNames.indexOf(it)
            if (position != -1) {
                binding.folderSpinner.setSelection(position)
            }
        }
    }
}
