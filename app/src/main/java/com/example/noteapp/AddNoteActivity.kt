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
import com.example.noteapp.databinding.ActivityAddNoteBinding
import com.example.noteapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.time.Month
import java.time.MonthDay
import java.time.Year
import java.util.Calendar
import java.util.Locale

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: NotesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = NotesDatabaseHelper(this)

        binding.dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        setupPrioritySpinner()


        binding.updatesaveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val date = binding.dateEditText.text.toString()
            val priority = binding.prioritySpinner.selectedItem.toString()
            val note = Note (0,title,content,date,priority)
            db.insertNote(note)
            finish()
            Toast.makeText(this,"Note Saved : $date", Toast.LENGTH_SHORT).show()

        }

    }

    private fun showDatePickerDialog(){
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day= calender.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =DatePickerDialog(this,{ _, year, monthOfYear, dayOfMonth ->
            val formatteddate = formatDate(year,monthOfYear,dayOfMonth)
            binding.dateEditText.setText(formatteddate)
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
        val adapter = PriorityAdapter(this, priorities)
        binding.prioritySpinner.adapter = adapter
    }

}

class PriorityAdapter(context: Context, priorities: Array<String>) :
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

