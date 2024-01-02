package com.dicoding.courseschedule.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.databinding.ActivityHomeBinding
import com.dicoding.courseschedule.ui.add.AddCourseActivity
import com.dicoding.courseschedule.ui.detail.DetailActivity
import com.dicoding.courseschedule.ui.list.ListActivity
import com.dicoding.courseschedule.ui.setting.SettingsActivity
import com.dicoding.courseschedule.util.DayName
import com.dicoding.courseschedule.util.QueryType
import com.dicoding.courseschedule.util.timeDifference

// TODO 15 : Write UI test to validate when user tap Add Course (+) Menu, the AddCourseActivity is displayed
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(DataRepository.getInstance(this))
    }
    private lateinit var binding: ActivityHomeBinding

    // TODO 5 : Show nearest schedule in CardHomeView and implement menu action
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.today_schedule)

        viewModel.getNearestSchedule().observe(this, { nearestSchedule ->
            if (nearestSchedule != null) {
                val dayName = DayName.getByNumber(nearestSchedule.day)
                val time = String.format(
                    getString(R.string.time_format),
                    dayName,
                    nearestSchedule.startTime,
                    nearestSchedule.endTime
                )
                val remainingTime = timeDifference(nearestSchedule.day, nearestSchedule.startTime)

                val cardHome = binding.viewHome

                cardHome.setCourseName(nearestSchedule.courseName)
                cardHome.setTime(time)
                cardHome.setRemainingTime(remainingTime)
                cardHome.setLecturer(nearestSchedule.lecturer)
                cardHome.setNote(nearestSchedule.note)

                cardHome.setOnClickListener {
                    onCourseClick(nearestSchedule)
                }

                binding.tvEmptyHome.visibility = View.GONE
            } else {
                // Handle case when there is no nearest schedule
                binding.tvEmptyHome.visibility = View.VISIBLE
            }
        })
    }

    private fun onCourseClick(course: Course) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.COURSE_ID, course.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                // Handle the "Add Course" action here
                // For example, start the AddCourseActivity
                startActivity(Intent(this, AddCourseActivity::class.java))
                return true
            }

            R.id.action_list -> {
                // Handle the "Show List" action here
                // For example, start the ListActivity
                startActivity(Intent(this, ListActivity::class.java))
                return true
            }

            R.id.action_settings -> {
                // Handle the "Settings" action here
                // For example, start the SettingsActivity
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

}
