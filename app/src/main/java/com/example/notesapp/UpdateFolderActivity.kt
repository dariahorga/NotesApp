//package com.example.notesapp
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.notesapp.databinding.ActivityUpdateFolderBinding
//
//class UpdateFolderActivity : AppCompatActivity(), NotesAdapter.OnItemClickListener {
//
//    private lateinit var binding: ActivityUpdateFolderBinding
//    private lateinit var db: NotesDatabaseHelper
//    private lateinit var folderName: String
//    private lateinit var folder: Folder
//    private lateinit var notesAdapter: NotesAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityUpdateFolderBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        db = NotesDatabaseHelper(this)
//
//        folderName = intent.getStringExtra("folder_name") ?: ""
//        if (folderName.isBlank()) {
//            Toast.makeText(this, "Folder name not provided", Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }
//
//        folder = db.getFolderIdByName(folderName) ?: run {
//            Toast.makeText(this, "Folder not found", Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }
//
//        binding.folderNameEditText.setText(folder.name)
//
//        setupRecyclerView()
//
//        binding.saveButton.setOnClickListener {
//            val newName = binding.folderNameEditText.text.toString().trim()
//
//            if (newName.isNotEmpty()) {
//                val updatedFolder = Folder(folder.id, newName)
//                db.updateFolder(updatedFolder)
//                Toast.makeText(this, "Folder updated", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Please enter a folder name", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        binding.deleteButton.setOnClickListener {
//            db.deleteFolder(folder.id)
//            Toast.makeText(this, "Folder deleted", Toast.LENGTH_SHORT).show()
//            finish()
//        }
//    }
//
//    private fun setupRecyclerView() {
//        val notes = db.getNotesByFolderId(folder.id)
//        notesAdapter = NotesAdapter(notes, folder, this)
//        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.notesRecyclerView.adapter = notesAdapter
//    }
//
//    override fun onItemClick(note: Note) {
//        // Implementați acțiunile dorite când utilizatorul face clic pe o notă
//    }
//}
