package com.example.noteapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class NotesDatabaseHelper(context:Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION){

    companion object{

        private const val DATABASE_NAME = "notesappss.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTEXT = "context"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_PRIOROTY = "priority"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTEXT TEXT, $COLUMN_DATE TEXT, $COLUMN_PRIOROTY TEXT)"""
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertNote(note: Note) {
        val db = writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_TITLE, note.title)
                put(COLUMN_CONTEXT, note.content)
                put(COLUMN_DATE, note.date)
                put(COLUMN_PRIOROTY,note.priority)
            }
            db.insertOrThrow(TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("NotesDatabaseHelper", "Error inserting note", e)
            // Optionally, rethrow the exception or handle it accordingly
        } finally {
            db.close()
        }
    }


    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {

            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTEXT))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIOROTY))

            val note = Note (id,title,content,date,priority)
            notesList.add(note)

        }

        cursor.close()
        db.close()
        return notesList
    }

    fun updateNote (note: Note){

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTEXT, note.content)
            put(COLUMN_DATE,note.date)
            put(COLUMN_PRIOROTY,note.priority)
        }

        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getNoteByID(noteId : Int): Note {

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $noteId "
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTEXT))
        val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
        val priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIOROTY))

        cursor.close()
        db.close()
        return Note(id,title,content,date,priority)
    }

    fun deleteNote(noteId: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()

    }
}