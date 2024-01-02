package com.dicoding.courseschedule.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dicoding.courseschedule.util.QueryType
import com.dicoding.courseschedule.util.SortType

// TODO 2 : Define data access object (DAO)
@Dao
interface CourseDao {

    @Query("SELECT * FROM ${DataCourseName.TABLE_NAME} ORDER BY ABS(:currentTime - (${DataCourseName.COL_DAY} * 24 * 60 + SUBSTR(${DataCourseName.COL_START_TIME}, 1, 2) * 60 + SUBSTR(${DataCourseName.COL_START_TIME}, 4, 2))) LIMIT 1")
    fun getNearestSchedule(currentTime: QueryType): LiveData<Course?>

    @Query(
        "SELECT * FROM ${DataCourseName.TABLE_NAME} ORDER BY CASE " +
                "WHEN :sortType = 'TIME' THEN ${DataCourseName.COL_START_TIME} " +
                "WHEN :sortType = 'COURSE_NAME' THEN ${DataCourseName.COL_COURSE_NAME} " +
                "WHEN :sortType = 'LECTURER' THEN ${DataCourseName.COL_LECTURER} END"
    )
    fun getAllCourse(sortType: SortType): DataSource.Factory<Int, Course>

    @Query("SELECT * FROM ${DataCourseName.TABLE_NAME} WHERE ${DataCourseName.COL_ID} = :id")
    fun getCourse(id: Int): LiveData<Course>

    @Query("SELECT * FROM ${DataCourseName.TABLE_NAME} WHERE ${DataCourseName.COL_DAY} = :day")
    fun getTodaySchedule(day: Int): List<Course>

    @Insert
    fun insert(course: Course)

    @Delete
    fun delete(course: Course)
}
