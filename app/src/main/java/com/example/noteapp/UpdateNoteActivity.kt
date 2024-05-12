package com.example.noteapp

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.noteapp.databinding.ActivityUpdateNoteBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set content view to the root view of the binding

        db = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        val note = db.getNoteByID(noteId)
        binding.updatetitleEditText.setText(note.title)
        binding.updatecontentEditText.setText(note.content)
        binding.updatedateEditText.setText(note.date)

        binding.updatedateEditText.setOnClickListener{
            showDatePickerDialog()
        }

        setupPrioritySpinner()

        binding.updatesaveButton.setOnClickListener {
            val newTitle = binding.updatetitleEditText.text.toString()
            val newCount = binding.updatecontentEditText.text.toString()
            val date=binding.updatedateEditText.text.toString()
            val prioroty = binding.editprioritySpinner.selectedItem.toString()
            if (newTitle.isNotEmpty() && newCount.isNotEmpty() && date.isNotEmpty()){
                val updateNote = Note(noteId, newTitle, newCount,date,prioroty)
                db.updateNote(updateNote)
                finish()
                Toast.makeText(this,"Changes Saved", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Please fil all fields",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun showDatePickerDialog(){
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day= calender.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =DatePickerDialog(this,{ _, year, monthOfYear, dayOfMonth ->
            val formatteddate = formatDate(year,monthOfYear,dayOfMonth)
            binding.updatedateEditText.setText(formatteddate)
        },year,month,day)

        datePickerDialog.show()
    }

    private fun formatDate(year: Int,month: Int,day: Int):String{
        val calendar= Calendar.getInstance()
        calendar.set(year,month,day)
        val format = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun setupPrioritySpinner() {
        val priorities = resources.getStringArray(R.array.priority_levels)
        val adapter = PriorityAdapter1(this, priorities)
        binding.editprioritySpinner.adapter = adapter
    }

}

class PriorityAdapter1(context: Context, priorities: Array<String>) :
    ArrayAdapter<String>(context, 0, priorities) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val view = recycledView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val textView: TextView = view.findViewById(R.id.text_view)
        val colorIndicator: ImageView = view.findViewById(R.id.color_indicator)

        textView.text = getItem(position)
        when (getItem(position)) {
            "Urgent" -> colorIndicator.setBackgroundResource(R.drawable.priority_circle_red)
            "Medium" -> colorIndicator.setBackgroundResource(R.drawable.priority_circle_green)
            "Not Important" -> colorIndicator.setBackgroundResource(R.drawable.priority_circle_shape)
        }

        return view
    }
}