package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityNotesInFolderBinding

class NotesInFolderActivity : AppCompatActivity() {

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

        // Extrage folderId și folderName din Intent
        folderId = intent.getIntExtra("folderId", -1)
        folderName = intent.getStringExtra("folderName")

        // Setează titlul activității cu numele folderului
        supportActionBar?.title = folderName

        // Obține toate notele din folderul specificat
        val notesInFolder = db.getAllNotes().filter { it.folderId == folderId }

        // Obține toate folderele pentru a afișa numele folderului în lista de note
        val folders = db.getAllFolders().associateBy({ it.id }, { it.name })

        // Inițializează adapterul RecyclerView cu notele și folderele
        notesAdapter = NotesAdapter(notesInFolder, folders, this)

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        // Setează un listener pentru butonul de adăugare notă
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java).apply {
                putExtra("folderId", folderId)  // Trimite folderId ca Extra
                putExtra("folderName", folderName) // Trimite folderName ca Extra (opțional)
            }
            startActivity(intent)
        }
    }
}
