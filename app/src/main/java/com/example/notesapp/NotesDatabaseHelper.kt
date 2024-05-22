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

        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_TASK_ID = "id"
        private const val COLUMN_TASK_CONTENT = "task_content"
        private const val COLUMN_TASK_DEADLINE = "task_deadline"
        private const val COLUMN_TASK_CHECKED = "task_checked"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createNotesTableQuery = "CREATE TABLE $TABLE_NOTES ($COLUMN_NOTE_ID INTEGER PRIMARY KEY, $COLUMN_NOTE_TITLE TEXT, $COLUMN_NOTE_CONTENT TEXT, $COLUMN_NOTE_FOLDER_ID INTEGER)"
        db.execSQL(createNotesTableQuery)

        val createFoldersTableQuery = "CREATE TABLE $TABLE_FOLDERS ($COLUMN_FOLDER_ID INTEGER PRIMARY KEY, $COLUMN_FOLDER_NAME TEXT)"
        db.execSQL(createFoldersTableQuery)

        val createTasksTableQuery = "CREATE TABLE $TABLE_TASKS ($COLUMN_TASK_ID INTEGER PRIMARY KEY, $COLUMN_TASK_CONTENT TEXT, $COLUMN_TASK_DEADLINE TEXT, $COLUMN_TASK_CHECKED INTEGER)"
        db.execSQL(createTasksTableQuery)

        val defaultFolderValues = ContentValues().apply {
            put(COLUMN_FOLDER_NAME, "Home")
        }
        db.insert(TABLE_FOLDERS, null, defaultFolderValues)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FOLDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }
    fun insertTask (task: Task)
    {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_TASK_CONTENT, task.content)
            put(COLUMN_TASK_DEADLINE, task.dataExpirare)
            put(COLUMN_TASK_CHECKED, 0)
        }
        db.insert(TABLE_TASKS, null, values)
        db.close()
    }
    fun updateTask(task: Task)
    {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_TASK_CONTENT, task.content)
            put(COLUMN_TASK_DEADLINE, task.dataExpirare)
            put(COLUMN_TASK_CHECKED, task.checked)
        }
        val whereClause = "$COLUMN_TASK_ID = ?"
        val whereArgs = arrayOf(task.id.toString())
        db.update(TABLE_TASKS, values, whereClause, whereArgs)
        db.close()
    }

    fun getAllTasks() : List<Task>
    {
        val tasksList = mutableListOf<Task>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_TASKS"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_CONTENT))
            val dataExp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DEADLINE))
            val check = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_CHECKED))
            val task = Task(id, content, dataExp, check)
            tasksList.add(task)
        }
        cursor.close()
        db.close()
        return tasksList
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

    fun deleteTask(taskId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_TASK_ID = ?"
        val whereArgs = arrayOf(taskId.toString())
        db.delete(TABLE_TASKS, whereClause, whereArgs)
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

    fun getTaskById(taskId: Int): Task? {
        val db = readableDatabase
        val selection = "$COLUMN_TASK_ID = ?"
        val selectionArgs = arrayOf(taskId.toString())
        val cursor = db.query(TABLE_TASKS, null, selection, selectionArgs, null, null, null)

        val task: Task?
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_CONTENT))
            val checked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_CHECKED))
            val deadline = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DEADLINE))
            task = Task(id, content, deadline, checked)
        } else {
            task = null
        }
        cursor.close()
        db.close()
        return task
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
