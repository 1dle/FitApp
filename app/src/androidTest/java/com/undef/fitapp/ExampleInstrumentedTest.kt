package com.undef.fitapp

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.undef.fitapp.models.UserData
import com.undef.fitapp.repositories.LoggedUserRepository
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

private const val REG_EMAIL = "test@asd.com"
private const val REG_PASSWORD = "you_never_guess_it"
private const val PEG_SEX = "Male"
private const val REG_BIRTHYEAR = "1970"
private const val REG_WEIGHT = 80.0
private const val REG_HEIGHT = 190.0
private const val REG_GOAL = R.id.rbGoalStay

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val activityRule = ActivityTestRule(CreateProfileActivity::class.java)


/*
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.undef.fitapp", appContext.packageName)
    }*/


    @Test
    fun registration_activity_works(){
        onView(withId(R.id.etRegEmail)).perform(typeText(REG_EMAIL))
        onView(withId(R.id.etRegPassword)).perform(typeText(REG_PASSWORD))

        //next page
        closeSoftKeyboard()
        onView(withId(R.id.btnCLDNext)).perform(click())


        onView(withId(R.id.etRegWeight)).perform(typeText(REG_WEIGHT.toString()))
        onView(withId(R.id.etRegHeight)).perform(typeText(REG_HEIGHT.toString()))

        //next page
        closeSoftKeyboard()
        onView(withId(R.id.btnMDNext)).perform(click())

        onView(withId(REG_GOAL)).perform(click())

        onView(withId(R.id.btnGDone)).perform(click())

        val reg_test_user = UserData(REG_BIRTHYEAR, REG_EMAIL, REG_HEIGHT, "male", "", REG_PASSWORD, "", REG_WEIGHT,"stay")

        onView(withId(R.id.textView)).check(matches(withText(reg_test_user.toString())))

    }
}
