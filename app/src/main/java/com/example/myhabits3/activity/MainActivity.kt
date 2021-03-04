package com.example.myhabits3.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myhabits3.model.Habit
import com.example.myhabits3.MainAdapter
import com.example.myhabits3.R
import com.example.myhabits3.model.SpacingItemDecoration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter: MainAdapter by lazy {
        val data = ArrayList<Habit>()
        MainAdapter(data, this) { habit, position ->

            val intent = Intent(this, AddAndEditActivity::class.java).run {
                putExtra(POSITION_INTENT_CODE, position)
                putExtra(HABIT_EDIT_INTENT_CODE, habit)
            }
            startActivityForResult(intent, EDIT_HABIT_INTENT_CODE)
        }
    }
    private var habits = arrayListOf<Habit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbarMainActivity)

        mainRecycler.addItemDecoration(
            SpacingItemDecoration(
                DISTANCE_BETWEEN_ELEMENTS
            )
        )
        mainRecycler.adapter = adapter

        mainFab.setOnClickListener {
            Intent(this, AddAndEditActivity::class.java).run {
                startActivityForResult(this, ADD_HABIT_INTENT_CODE)
            }

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putSerializable(CONFIG_CHANGE_HABITS_CODE, adapter.habits)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        habits =
            savedInstanceState.getSerializable(CONFIG_CHANGE_HABITS_CODE) as ArrayList<Habit> //TODO as?
        if (habits.size != 0) {
            adapter.addListOfHabits(habits)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_HABIT_INTENT_CODE && resultCode == Activity.RESULT_OK) {

            adapter.addItem(data?.getParcelableExtra(AddAndEditActivity.NEW_HABIT_INTENT_CODE)!!)
        } else if (requestCode == EDIT_HABIT_INTENT_CODE) {
            adapter.changeItem(
                data?.getParcelableExtra(AddAndEditActivity.NEW_HABIT_INTENT_CODE)!!,
                data.getIntExtra(AddAndEditActivity.POSITION_INTENT_CODE, 0)
            )
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val ADD_HABIT_INTENT_CODE = 12
        const val EDIT_HABIT_INTENT_CODE = 13
        const val POSITION_INTENT_CODE = "position"
        const val HABIT_EDIT_INTENT_CODE = "object"
        const val CONFIG_CHANGE_HABITS_CODE = "list of habits"
        const val DISTANCE_BETWEEN_ELEMENTS = 50 //between elements in recyclerView

    }

}