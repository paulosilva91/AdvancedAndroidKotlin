import android.app.Activity
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import kotlinx.coroutines.runBlocking
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    private lateinit var tasksRepository: TasksRepository

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        tasksRepository = ServiceLocator.provideTasksRepository(getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


    @Test
    fun tasksScreen_clickOnDrawerIcon_OpensNavigation() {
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
        onView(
                withContentDescription(
                        activityScenario
                                .getToolbarNavigationContentDescription()
                )
        ).perform(click())
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.START)))
        activityScenario.close()
        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleUpButton() = runBlocking {
        val task = Task("Up button", "Description")
        tasksRepository.saveTask(task)
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withText("Up button")).perform(click())
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(
                withContentDescription(
                        activityScenario
                                .getToolbarNavigationContentDescription()
                )
        ).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(
                withContentDescription(
                        activityScenario
                                .getToolbarNavigationContentDescription()
                )
        ).perform(click())
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))
        activityScenario.close()
    }


    @Test
    fun taskDetailScreen_doubleBackButton() = runBlocking {
        val task = Task("Back button", "Description")
        tasksRepository.saveTask(task)
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withText("Back button")).perform(click())
        onView(withId(R.id.edit_task_fab)).perform(click())
        pressBack()
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))
        activityScenario.close()
    }

}

fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription()
        : String {
    var description = ""
    onActivity {
        description =
                it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
    }
    return description
}