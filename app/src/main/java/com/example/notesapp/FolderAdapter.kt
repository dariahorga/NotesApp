package com.example.notesapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.FolderItemBinding

class FolderAdapter(
    private var folders: List<Folder>,
    private val context: Context,
    private val updateFolder: (String) -> Unit,
    private val deleteFolderCallback: (Int) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(private val binding: FolderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: Folder) {
            // setam numele folderului in text view
            binding.folderNameTextView.text = folder.name

            // setam click listener pe butonul de editare folder
            binding.editFolderButton.setOnClickListener {
                updateFolder(folder.name)
            }

            // setam click listener pe butonul de stergere folder
            binding.deleteFolderButton.setOnClickListener {
                deleteFolderCallback(folder.id)
            }

            // setam click listener pe item view pentru a deschide activitatea NotesInFolder
            itemView.setOnClickListener {
                val intent = Intent(context, NotesInFolderActivity::class.java).apply {
                    putExtra("folderId", folder.id)
                    putExtra("folderName", folder.name)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding =
            FolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.bind(folder)
    }

    // metoda pentru actualizarea datelor in adapter
    fun refreshData(newFolders: List<Folder>) {
        folders = newFolders
        notifyDataSetChanged()
    }

}
