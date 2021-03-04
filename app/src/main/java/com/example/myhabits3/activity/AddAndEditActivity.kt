package com.example.myhabits3.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.myhabits3.ColorPickerDialogFragment
import com.example.myhabits3.model.Habit
import com.example.myhabits3.R
import com.example.myhabits3.model.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_add_and_edit.*
import java.util.*

class AddAndEditActivity : AppCompatActivity(), ColorPickerDialogFragment.IChangeColor {

    companion object {
        const val NEW_HABIT_INTENT_CODE = "new"
        const val POSITION_INTENT_CODE = "position"
        const val COLOR_CONFIG_CHANGE_CODE = "current color"
        const val COLOR_NUM_CONFIG_CHANGE_CODE = "current color number"
    }

    private var curColor: Int = Util.intColors[16]!!
    private var curColorNumber = ColorPickerDialogFragment.DEFAULT_COLOR

    private lateinit var priorities: Array<String>
    private lateinit var periods: Array<String>

    private var curExtras: Bundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_edit)

        setSupportActionBar(toolbarAddAndEdit)

        priorities = applicationContext.resources.getStringArray(R.array.priorities)
        periods = applicationContext.resources.getStringArray(R.array.periods)

        curExtras = intent.extras

        if (intent.extras == null) {
            initAdding()
        } else {
            initEditing()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.saveHabit -> {
            if (intent.extras == null) {
                addHabit()
            } else {
                editHabit()
            }

            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.are_you_sure_you_want_to_exit)
            .setMessage(R.string.habit_will_not_be_saved)
            .setPositiveButton(R.string.yes) { _, _ ->
                intent.extras?.let {
                    val intent = Intent(this, MainActivity::class.java).run {
                        putExtra(
                            NEW_HABIT_INTENT_CODE,
                            intent.getSerializableExtra(MainActivity.HABIT_EDIT_INTENT_CODE) as Habit
                        )
                        putExtra(
                            POSITION_INTENT_CODE,
                            intent.getIntExtra(MainActivity.POSITION_INTENT_CODE, 0)
                        )
                    }

                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                super.onBackPressed()
            }
            .setNegativeButton(R.string.no) { _, _ -> }
            .setCancelable(true)
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putInt(COLOR_CONFIG_CHANGE_CODE, curColor)
        outState.putInt(COLOR_NUM_CONFIG_CHANGE_CODE, curColorNumber)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        curColor = savedInstanceState.getInt(COLOR_CONFIG_CHANGE_CODE)
        curColorNumber = savedInstanceState.getInt(COLOR_NUM_CONFIG_CHANGE_CODE)
        curColorImageView.setColorFilter(curColor)

        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun addHabit() {
        if (canAddOrEdit()) {

            val newHabit = Habit(
                title = habitNameAddAndEdit.text.toString(),
                description = habitDescriptionAddAndEdit.text.toString(),
                priority = if (habitPriorityAddAndEdit.text.toString().isNotEmpty()) {
                    priorities.indexOf(habitPriorityAddAndEdit.text.toString())
                } else {
                    0
                },
                type = radioButtonGood.isChecked.toInt(),
                count = if (habitDoneAddEdit.text.toString() == "") {
                    0
                } else {
                    habitDoneAddEdit.text.toString().toInt()
                },
                frequency = if (habitPeriodAddAndEdit.text.toString().isNotEmpty()) {
                    periods.indexOf(habitPeriodAddAndEdit.text.toString())
                } else {
                    2
                },
                color = curColor,
                date = Calendar.getInstance().time.time,
                doneDates = mutableListOf()
            )


            val intent = Intent(this, MainActivity::class.java).run {
                putExtra(NEW_HABIT_INTENT_CODE, newHabit)
            }

            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun editHabit() {

        val habitToEdit = intent.getSerializableExtra(MainActivity.HABIT_EDIT_INTENT_CODE) as Habit
        val position = intent.getIntExtra(MainActivity.POSITION_INTENT_CODE, 0)
        println("position - $position")

        val newHabit = Habit(
            title = habitNameAddAndEdit.text.toString(),
            description = habitDescriptionAddAndEdit.text.toString(),
            priority = if (habitPriorityAddAndEdit.text.toString().isNotEmpty()) {
                priorities.indexOf(habitPriorityAddAndEdit.text.toString())
            } else {
                0
            },
            type = radioButtonGood.isChecked.toInt(),
            count = if (habitDoneAddEdit.text.toString() == "") {
                0
            } else {
                habitDoneAddEdit.text.toString().toInt()
            },
            frequency = if (habitPeriodAddAndEdit.text.toString().isNotEmpty()) {
                periods.indexOf(habitPeriodAddAndEdit.text.toString())
            } else {
                2
            },
            color = curColor,
            date = habitToEdit.date,
            doneDates = habitToEdit.doneDates
        )


        val intent = Intent(this, MainActivity::class.java).run {
            putExtra(NEW_HABIT_INTENT_CODE, newHabit)
            putExtra(POSITION_INTENT_CODE, position)
        }

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun init() {

        val adapterPriority = ArrayAdapter(this, R.layout.list_item, priorities)
        (habitPriorityInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterPriority)
        habitPriorityAddAndEdit.keyListener = null

        val adapterPeriod = ArrayAdapter(this, R.layout.list_item, periods)
        (habitPeriodInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterPeriod)
        habitPeriodAddAndEdit.keyListener = null

        colorPickerButton.setOnClickListener {
            colorPickersOnClick()
        }
        curColorImageView.setOnClickListener {
            colorPickersOnClick()
        }

        curColorImageView.setColorFilter(curColor)
    }

    private fun colorPickersOnClick() {
        println(curColor)
        println(Util.getColorNumberByColor(curColor))

        ColorPickerDialogFragment.newInstance(Util.getColorNumberByColor(curColor))
            .show(supportFragmentManager, ColorPickerDialogFragment.TAG)
    }

    private fun initAdding() {
        init()

        toolbarAddAndEdit.setTitle(R.string.label_add)
    }

    private fun initEditing() {

        init()

        toolbarAddAndEdit.setTitle(R.string.label_edit)

        val habitToEdit = intent.getSerializableExtra(MainActivity.HABIT_EDIT_INTENT_CODE) as Habit

        habitNameAddAndEdit.setText(habitToEdit.title)
        habitDescriptionAddAndEdit.setText((habitToEdit.description))

        if (habitToEdit.type.toBoolean()) {
            radioButtonGood.isChecked = true
        } else {
            radioButtonBad.isChecked = true
        }

        habitDoneAddEdit.setText(habitToEdit.count.toString())

        habitPriorityAddAndEdit.setText(priorities[habitToEdit.priority])
        habitPeriodAddAndEdit.setText(periods[habitToEdit.priority])

        curColor = habitToEdit.color

        curColorImageView.setColorFilter(habitToEdit.color)
    }

    private fun canAddOrEdit(): Boolean {
        var isAble = true
        if (habitNameAddAndEdit.text?.isEmpty() == true) {
            habitNameInputLayout.error = resources.getString(R.string.Field_must_not_be_empty)
            isAble = false
        } else {
            habitNameInputLayout.error = ""
        }
        if (habitDescriptionAddAndEdit.text?.isEmpty() == true) {
            habitDescriptionInputLayout.error =
                resources.getString(R.string.Field_must_not_be_empty)
            isAble = false
        } else {
            habitDescriptionInputLayout.error = ""
        }
        if (habitPeriodAddAndEdit.text?.isEmpty() == true) {
            habitPeriodInputLayout.error = resources.getString(R.string.Field_must_not_be_empty)
            isAble = false
        } else {
            habitPeriodInputLayout.error = ""
        }
        return isAble
    }


    override fun onChangeColor(newColor: Int, newColorNumber: Int) {
        curColor = newColor
        curColorNumber = newColorNumber

        curColorImageView.setColorFilter(newColor)
    }


    private fun Boolean.toInt() = if (this) 1 else 0

    private fun Int.toBoolean(): Boolean = this == 1

}