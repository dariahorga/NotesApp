package com.example.notesapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "notesapp.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_NOTES = "notes"
        private const val COLUMN_NOTE_ID = "id"
        private const val COLUMN_NOTE_TITLE = "title"
        private const val COLUMN_NOTE_CONTENT = "content"
        private const val COLUMN_NOTE_FOLDER_ID = "folder_id"

        private const val TABLE_FOLDERS = "folders"
        private const val COLUMN_FOLDER_ID = "id"
        private const val COLUMN_FOLDER_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createNotesTableQuery = "CREATE TABLE $TABLE_NOTES ($COLUMN_NOTE_ID INTEGER PRIMARY KEY, $COLUMN_NOTE_TITLE TEXT, $COLUMN_NOTE_CONTENT TEXT, $COLUMN_NOTE_FOLDER_ID INTEGER)"
        db.execSQL(createNotesTableQuery)

        val createFoldersTableQuery = "CREATE TABLE $TABLE_FOLDERS ($COLUMN_FOLDER_ID INTEGER PRIMARY KEY, $COLUMN_FOLDER_NAME TEXT)"
        db.execSQL(createFoldersTableQuery)

        val defaultFolderValues = ContentValues().apply {
            put(COLUMN_FOLDER_NAME, "Home")
        }
        db.insert(TABLE_FOLDERS, null, defaultFolderValues)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FOLDERS")
        onCreate(db)
    }
    fun insertNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TITLE, note.title)
            put(COLUMN_NOTE_CONTENT, note.content)
            put(COLUMN_NOTE_FOLDER_ID, note.folderId)
        }
        db.insert(TABLE_NOTES, null, values)
        db.close()
    }

    fun updateNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TITLE, note.title)
            put(COLUMN_NOTE_CONTENT, note.content)
            put(COLUMN_NOTE_FOLDER_ID, note.folderId)
        }
        val whereClause = "$COLUMN_NOTE_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NOTES, values, whereClause, whereArgs)
        db.close()
    }

    fun deleteNote(noteId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_NOTE_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NOTES, whereClause, whereArgs)
        db.close()
    }

    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NOTES"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT))
            val folderId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_FOLDER_ID))
            val note = Note(id, title, content, folderId)
            notesList.add(note)
        }
        cursor.close()
        db.close()
        return notesList
    }

    fun getNoteById(noteId: Int): Note? {
        val db = readableDatabase
        val selection = "$COLUMN_NOTE_ID = ?"
        val selectionArgs = arrayOf(noteId.toString())
        val cursor = db.query(TABLE_NOTES, null, selection, selectionArgs, null, null, null)

        val note: Note?
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT))
            val folderId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_FOLDER_ID))
            note = Note(id, title, content, folderId)
        } else {
            note = null
        }

        cursor.close()
        db.close()
        return note
    }

    fun getAllFolders(): List<Folder> {
        val foldersList = mutableListOf<Folder>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_FOLDERS"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOLDER_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOLDER_NAME))
            val folder = Folder(id, name)
            foldersList.add(folder)
        }
        cursor.close()
        db.close()
        return foldersList
    }

    fun getFolderIdByName(folderName: String): Int {
        val db = readableDatabase
        val selection = "$COLUMN_FOLDER_NAME = ?"
        val selectionArgs = arrayOf(folderName)
        val cursor = db.query(TABLE_FOLDERS, null, selection, selectionArgs, null, null, null)

        val folderId: Int
        if (cursor.moveToFirst()) {
            folderId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOLDER_ID))
        } else {
            folderId = -1
        }

        cursor.close()
        db.close()
        return folderId
    }

    fun insertFolder(folder: Folder) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FOLDER_NAME, folder.name)
        }
        db.insert(TABLE_FOLDERS, null, values)
        db.close()
    }
}
