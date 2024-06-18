package com.example.notesapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.FolderItemBinding

class FolderAdapter(private var folders: List<Folder>, private val context: Context) :
    RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    // ViewHolder pentru elementele din RecyclerView
    inner class FolderViewHolder(private val binding: FolderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: Folder) {
            binding.folderNameTextView.text = folder.name

            itemView.setOnClickListener {
                val intent = Intent(context, NotesInFolderActivity::class.java).apply {
                    putExtra("folderId", folder.id)
                    putExtra("folderName", folder.name)
                }
                context.startActivity(intent)
            }
        }
    }

    // Crearea unui ViewHolder pentru fiecare element din RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = FolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    // Returnează numărul de elemente din lista de foldere
    override fun getItemCount(): Int {
        return folders.size
    }

    // Legarea datelor din lista de foldere cu elementele din ViewHolder
    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.bind(folder)
    }

    // Actualizarea datelor din adaptor și notificarea RecyclerView-ului
    fun refreshData(newFolders: List<Folder>) {
        folders = newFolders
        notifyDataSetChanged()
    }
}
