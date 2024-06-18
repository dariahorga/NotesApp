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

class FolderAdapter(private var folders: List<Folder>, private val context: Context) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    // Interfață pentru gestionarea evenimentului de clic pe item
    interface OnItemClickListener {
        fun onItemClick(folder: Folder)
    }

    // Setează listener-ul pentru item-ul din RecyclerView
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    // ViewHolder pentru elementele din RecyclerView
    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderNameTextView: TextView = itemView.findViewById(R.id.folderNameTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    // Crearea unui ViewHolder pentru fiecare element din RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_item, parent, false)
        return FolderViewHolder(view)
    }

    // Returnează numărul de elemente din lista de foldere
    override fun getItemCount(): Int = folders.size

    // Legarea datelor din lista de foldere cu elementele din ViewHolder
    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]

        holder.folderNameTextView.text = folder.name

//        // Click listener pentru butonul de editare
//        holder.updateButton.setOnClickListener {
//            val intent = Intent(holder.itemView.context, UpdateFolderActivity::class.java).apply {
//                putExtra("folder_id", folder.id)
//            }
//            holder.itemView.context.startActivity(intent)
//        }

        // Click listener pentru butonul de ștergere
        holder.deleteButton.setOnClickListener {
            val db = NotesDatabaseHelper(holder.itemView.context)
            db.deleteFolder(folder.id)
            refreshData(db.getAllFolders())
            Toast.makeText(holder.itemView.context, "Folder Deleted", Toast.LENGTH_SHORT).show()
        }

        // Click listener pentru elementul de tip folder
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(folder)
        }
    }

    // Actualizarea datelor din adaptor și notificarea RecyclerView-ului
    fun refreshData(newFolders: List<Folder>) {
        folders = newFolders
        notifyDataSetChanged()
    }
}
