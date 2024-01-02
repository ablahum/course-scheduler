package com.dicoding.courseschedule.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.databinding.ActivityAddCourseBinding
import com.dicoding.courseschedule.databinding.ActivityDetailBinding
import com.dicoding.courseschedule.util.DayName
import com.dicoding.courseschedule.util.TimePickerFragment
import java.util.*

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private val viewModel: AddCourseViewModel by viewModels {
        AddCourseViewModelFactory(DataRepository.getInstance(this))
    }
    private lateinit var binding: ActivityAddCourseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeDaySpinner()

        binding.ibStartTime.setOnClickListener { showTimePicker("start_time") }
        binding.ibEndTime.setOnClickListener { showTimePicker("end_time") }

        observeViewModel()
    }

    private fun initializeDaySpinner() {
        val daysArray = resources.getStringArray(R.array.day)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDay.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                save()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun save() {
        val courseName = binding.addEdTitle.text.toString()
        val day = binding.spinnerDay.selectedItemPosition
        val startTime = binding.tvStartTime.text.toString()
        val endTime = binding.tvEndTime.text.toString()
        val lecturer = binding.tvLecturer.text.toString()
        val note = binding.addEdDescription.text.toString()

        viewModel.insertCourse(courseName, day, startTime, endTime, lecturer, note)
    }

    private fun observeViewModel() {
        viewModel.saved.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { isSaved ->
                if (isSaved) {
                    finish()
                } else {
                    // Handle failure case if needed
                }
            }
        })
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        if (tag == "start_time") {
            binding.tvStartTime.text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        } else if (tag == "end_time") {
            binding.tvEndTime.text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }
    }

    private fun showTimePicker(tag: String) {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.show(supportFragmentManager, tag)
    }
}
