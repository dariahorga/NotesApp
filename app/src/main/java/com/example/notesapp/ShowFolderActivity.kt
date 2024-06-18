package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityShowFolderBinding

class ShowFolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowFolderBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var folderAdapter: FolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)
        folderAdapter = FolderAdapter(emptyList(), this)

        binding.foldersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ShowFolderActivity)
            adapter = folderAdapter
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddFolderActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        displayFolders()
    }

    private fun displayFolders() {
        val folders = db.getAllFolders()
        folderAdapter.refreshData(folders)

        folderAdapter.setOnItemClickListener(object : FolderAdapter.OnItemClickListener {
            override fun onItemClick(folder: Folder) {
                // La apăsarea unui folder, deschide activitatea pentru afișarea notițelor din acel folder
                openNotesActivity(folder.id)
            }
        })
    }

    private fun openNotesActivity(folderId: Int) {
        val intent = Intent(this, ShowNotesActivity::class.java).apply {
            putExtra("folder_id", folderId)
        }
        startActivity(intent)
    }
}
