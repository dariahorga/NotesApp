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
            binding.folderNameTextView.text = folder.name

            binding.editFolderButton.setOnClickListener {
                updateFolder(folder.name)
            }

            binding.deleteFolderButton.setOnClickListener {
                deleteFolderCallback(folder.id)
            }

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

    fun refreshData(newFolders: List<Folder>) {
        folders = newFolders
        notifyDataSetChanged()
    }

}

