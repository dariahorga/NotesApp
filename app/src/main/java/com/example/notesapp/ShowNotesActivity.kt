package com.example.notesapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityShowNotesBinding

class ShowNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowNotesBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initializam baza de date
        db = NotesDatabaseHelper(this)

        val folderId = intent.getIntExtra("folder_id", -1)
        if (folderId == -1) {
            finish()
            return
        }

        // definim functia restoreNoteCallback
        val restoreNoteCallback: (Int) -> Unit = { noteId ->
            val note = db.getNoteById(noteId)
            note?.let {
                notesAdapter.refreshData(db.getNotesByFolderId(folderId))
                Toast.makeText(this, "Note Restored: ${it.title}", Toast.LENGTH_SHORT).show()
            }
        }

        // pasam restoreNoteCallback catre constructorul NotesAdapter
        notesAdapter = NotesAdapter(emptyList(), emptyMap(), this, restoreNoteCallback)
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        // afisam notele din folderul specificat
        displayNotes(folderId)
    }

    private fun displayNotes(folderId: Int) {
        // obtinem notele din folderul specificat si actualizam adapterul
        val notes = db.getNotesByFolderId(folderId)
        notesAdapter.refreshData(notes)
    }
}
