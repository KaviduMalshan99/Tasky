package com.example.noteapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter (private var notes:List<Note>, context:Context) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val db: NotesDatabaseHelper = NotesDatabaseHelper(context)

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tittleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val dateTextView : TextView = itemView.findViewById(R.id.dateTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton : ImageView = itemView.findViewById(R.id.deleteButton)
        val priorityIndicator : View = itemView.findViewById(R.id.priorityIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent,false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.tittleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.dateTextView.text = "Date: ${note.date}"

        // Set the priority indicator color based on the note's priority
        when (note.priority) {
            "Urgent" -> holder.priorityIndicator.setBackgroundResource(R.drawable.priority_circle_red)
            "Medium" -> holder.priorityIndicator.setBackgroundResource(R.drawable.priority_circle_green)
            "Not Important" -> holder.priorityIndicator.setBackgroundResource(R.drawable.priority_circle_shape)
            else -> holder.priorityIndicator.visibility = View.VISIBLE

        }

        // Set the onClickListener for the update button
        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateNoteActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        // Set the onClickListener for the delete button
        holder.deleteButton.setOnClickListener {
            db.deleteNote(note.id)
            refreshData(db.getAllNotes())
            Toast.makeText(holder.itemView.context, "Note Deleted", Toast.LENGTH_SHORT).show()
        }
    }


    fun refreshData (newNotes: List<Note>){

        notes = newNotes
        notifyDataSetChanged()
    }

}