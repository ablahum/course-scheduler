package test

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.dicoding.courseschedule.ui.list.ListActivity
import org.junit.After
import org.junit.Rule
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.ui.add.AddCourseActivity
import org.junit.Test

class ListActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ListActivity::class.java)

    @Test
    fun clickFab_OpenAddTaskActivity() {
        // Initialize Espresso Intents
        Intents.init()

        // Launch the activity manually
        activityRule.scenario.onActivity { /* Do nothing, just ensure activity is launched */ }

        // Click on the FAB to open AddTaskActivity
        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())

        // Verify that the correct intent to launch AddTaskActivity is sent
        Intents.intended(hasComponent(AddCourseActivity::class.java.name))

        // Release Espresso Intents
        Intents.release()
    }
}
