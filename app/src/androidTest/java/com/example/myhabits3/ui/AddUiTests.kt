package com.example.myhabits3.ui

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.agoda.kakao.common.utilities.getResourceString
import com.agoda.kakao.edit.TextInputLayoutAssertions
import com.example.myhabits3.R
import com.example.myhabits3.ui.activity.MainActivity
import com.google.android.material.textfield.TextInputLayout
import org.junit.Rule
import org.junit.Test

@LargeTest
class AddUiTests {

    @Rule
    @JvmField
    val rule = ActivityScenarioRule(MainActivity::class.java)

    private val screen = AddScreen()

    @Test
    fun add_screen_empty_test() {

        screen {

            addButtonMain.click()

            addOrEditButton.click()

            nameInput.hasError(getResourceString(R.string.Field_must_not_be_empty))

            descriptionInput.hasError(getResourceString(R.string.Field_must_not_be_empty))

            periodInput.hasError(getResourceString(R.string.Field_must_not_be_empty))


            //-----------------------------------------------

            nameInput.edit.typeText("name")
            Espresso.closeSoftKeyboard()

            addOrEditButton.click()

            nameInput.checkErrorIsNull()

            descriptionInput.hasError(getResourceString(R.string.Field_must_not_be_empty))

            periodInput.hasError(getResourceString(R.string.Field_must_not_be_empty))


            //-----------------------------------------------

            descriptionInput.edit.typeText("description")
            Espresso.closeSoftKeyboard()

            addOrEditButton.click()

            nameInput.checkErrorIsNull()

            descriptionInput.checkErrorIsNull()

            periodInput.hasError(getResourceString(R.string.Field_must_not_be_empty))

            //-----------------------------------------------


            periodInput {
                click()
                Espresso
                    .onView(ViewMatchers.withText("Per day"))
                    .inRoot(RootMatchers.isPlatformPopup())
                    .perform(ViewActions.click())
            }
            Espresso.closeSoftKeyboard()

            priorityInput {
                click()
                Espresso
                    .onView(ViewMatchers.withText("High"))
                    .inRoot(RootMatchers.isPlatformPopup())
                    .perform(ViewActions.click())
            }
            Espresso.closeSoftKeyboard()

            countView.typeText("23")
            Espresso.closeSoftKeyboard()

            addOrEditButton.click()

            recycler {
                childAt<AddScreen.Item>(0) {
                    title.hasText("name")
                    description.hasText("description")
                    period.hasText("23 ${getResourceString(R.string.times)} Per day")
                    priority.hasText("High ${getResourceString(R.string.priority)}")
                }
            }
        }

    }

    private fun TextInputLayoutAssertions.checkErrorIsNull() {
        view.check { view, notFoundException ->
            if (view is TextInputLayout) {
                if (view.error != null) {
                    throw AssertionError(
                        "Expected error is null," +
                                " but actual is ${view.error}"
                    )
                }
            } else {
                notFoundException?.let { throw AssertionError(it) }
            }
        }
    }

}