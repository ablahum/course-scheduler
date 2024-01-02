package com.dicoding.courseschedule.paging

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.util.DayName.Companion.getByNumber

class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private lateinit var course: Course
    private val timeString = itemView.context.resources.getString(R.string.time_format)
    private val courseNameTextView : TextView = itemView.findViewById(R.id.tv_course)
    private val timeTextView : TextView = itemView.findViewById(R.id.tv_time)
    private val lecturerTextView : TextView = itemView.findViewById(R.id.tv_lecturer)
    // TODO 7 : Complete ViewHolder to show item
    fun bind(course: Course, clickListener: (Course) -> Unit) {
        this.course = course

        itemView.apply {
            val dayName = getByNumber(course.day)
            val timeFormat = String.format(timeString, dayName, course.startTime, course.endTime)
            courseNameTextView.text = course.courseName
            timeTextView.text = timeFormat
            lecturerTextView.text = course.lecturer

            setOnClickListener {
                clickListener(course)
            }
        }
    }

    fun getCourse(): Course = course
}
