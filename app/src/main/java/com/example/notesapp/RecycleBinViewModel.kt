package com.example.notesapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecycleBinViewModel(private val db: NotesDatabaseHelper) : ViewModel() {

    private val _deletedNotes = MutableLiveData<List<Note>>()
    val deletedNotes: LiveData<List<Note>> get() = _deletedNotes

    init {
        loadDeletedNotes()
    }

    private fun loadDeletedNotes() {
        _deletedNotes.value = db.getNotesInRecycleBin()
    }

    fun restoreNoteFromRecycleBin(noteId: Int) {
        db.restoreNoteFromRecycleBin(noteId)
        loadDeletedNotes()
    }
}
