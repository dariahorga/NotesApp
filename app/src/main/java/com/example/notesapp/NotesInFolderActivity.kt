package com.example.notesapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityNotesInFolderBinding

class NotesInFolderActivity : AppCompatActivity() {

    companion object {
        private const val ADD_NOTE_REQUEST = 1
        private const val UPDATE_NOTE_REQUEST = 2
    }
    private lateinit var binding: ActivityNotesInFolderBinding
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var db: NotesDatabaseHelper
    private var folderId: Int = -1
    private var folderName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesInFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        folderId = intent.getIntExtra("folderId", -1)
        folderName = intent.getStringExtra("folderName")

        supportActionBar?.title = folderName

        val notesInFolder = db.getNotesByFolderId(folderId)

        val folders = db.getAllFolders().associateBy({ it.id }, { it.name })

        notesAdapter = NotesAdapter(notesInFolder, folders, this){

        }

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java).apply {
                putExtra("folderId", folderId)  // Trimite folderId ca Extra
                putExtra("folderName", folderName) // Trimite folderName ca Extra (opțional)
            }
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            refreshNotesList()
        }else if (requestCode == UPDATE_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            refreshNotesList()
        }
    }
    private fun refreshNotesList() {
        val notesInFolder = db.getNotesByFolderId(folderId)

        notesAdapter.refreshData(notesInFolder)
    }
}


